package com.hzoom.core.es.config;

import com.alibaba.fastjson.JSON;
import com.hzoom.core.es.vo.ElasticEntity;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
public class BaseElasticService {
    @Autowired
    RestHighLevelClient client;
    @Autowired
    EsProperties esProperties;

    /**
     * @param idxName 索引名称
     * @param idxSQL  索引描述
     * @return void
     * @throws
     * @See
     * @since
     */
    public void createIndex(String idxName, String idxSQL, String setSQL) {
        try {
            if (!this.indexExist(idxName)) {
                log.error(" idxName={} 已经存在,idxSql={},setSQL = {}", idxName, idxSQL, setSQL);
                return;
            }
            CreateIndexRequest request = new CreateIndexRequest(idxName);
            request.mapping(idxSQL, XContentType.JSON);
            request.settings(setSQL, XContentType.JSON);
            CreateIndexResponse res = client.indices().create(request, RequestOptions.DEFAULT);
            if (!res.isAcknowledged()) {
                throw new RuntimeException("初始化失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断索引是否存在
     *
     * @param idxName
     * @return
     * @throws Exception
     */
    public boolean indexExist(String idxName) throws Exception {
        GetIndexRequest request = new GetIndexRequest(idxName);
        request.local(false);
        request.humanReadable(true);
        request.includeDefaults(false);
        request.indicesOptions(IndicesOptions.lenientExpandOpen());
        return client.indices().exists(request, RequestOptions.DEFAULT);
    }

    /**
     * 判断索引是否存在
     *
     * @param idxName
     * @return
     * @throws Exception
     */
    public boolean isExistsIndex(String idxName) throws Exception {
        return client.indices().exists(new GetIndexRequest(idxName), RequestOptions.DEFAULT);
    }


    /**
     * 更新数据
     *
     * @param idxName
     * @param entity
     */
    public void insertOrUpdateOne(String idxName, ElasticEntity entity) {
        IndexRequest request = new IndexRequest(idxName);
        log.error("Data : id={},entity={}", entity.getId(), JSON.toJSONString(entity.getData()));
        request.id(entity.getId());
        request.source(entity.getData(), XContentType.JSON);
//        request.source(JSON.toJSONString(entity.getData()), XContentType.JSON);
        try {
            client.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量插入数据
     *
     * @param idxName index
     * @param list    带插入列表
     * @return void
     * @throws
     * @since
     */
    public void insertBatch(String idxName, List<ElasticEntity> list) {
        BulkRequest request = new BulkRequest();
        list.forEach(item -> request.add(new IndexRequest(idxName).id(item.getId())
                .source(item.getData(), XContentType.JSON)));
        try {
            client.bulk(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 搜索
     *
     * @param idxName index
     * @param builder 查询参数
     * @param clazz   结果类对象
     * @return java.util.List<T>
     * @throws
     * @since
     */
    public <T> List<T> query(String idxName, SearchSourceBuilder builder, Class<T> clazz) {
        SearchRequest request = new SearchRequest(idxName);
        request.source(builder);
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            SearchHit[] hits = response.getHits().getHits();
            List<T> res = new ArrayList<>(hits.length);
            for (SearchHit hit : hits) {
                res.add(JSON.parseObject(hit.getSourceAsString(), clazz));
            }
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public List queryDoc(String index, QueryBuilder queryBuilder) {
        List list = new ArrayList();
        try {
            SearchRequest request = new SearchRequest(index);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(queryBuilder);
            request.source(sourceBuilder);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();

            for (SearchHit hit : hits) {
                Object obj = JSON.parse(hit.getSourceAsString());
                list.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List queryDoc(String index, QueryBuilder queryBuilder, List<AggregationBuilder> aggregationBuilders) {
        List list = new ArrayList();
        try {
            SearchRequest request = new SearchRequest(index);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(queryBuilder);
            for (AggregationBuilder aggregationBuilder : aggregationBuilders) {
                sourceBuilder.aggregation(aggregationBuilder);
            }
            request.source(sourceBuilder);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();

            for (SearchHit hit : hits) {
                Object obj = JSON.parse(hit.getSourceAsString());
                list.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public SearchHits queryDetailDoc(String index, QueryBuilder queryBuilder) {
        try {
            SearchRequest request = new SearchRequest(index);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(queryBuilder);
            request.source(sourceBuilder);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            return hits;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除index
     *
     * @param idxName
     * @return void
     * @throws
     * @since
     */
    public void deleteIndex(String idxName) {
        try {
            if (!this.indexExist(idxName)) {
                log.error(" idxName={} 不存在", idxName);
                return;
            }
            client.indices().delete(new DeleteIndexRequest(idxName), RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param idxName
     * @param builder
     * @return void
     * @throws
     * @since
     */
    public void deleteByQuery(String idxName, QueryBuilder builder) {
        DeleteByQueryRequest request = new DeleteByQueryRequest(idxName);
        request.setQuery(builder);
        //设置批量操作数量,最大为10000
        request.setBatchSize(10000);
        request.setConflicts("proceed");
        try {
            BulkByScrollResponse bulkByScrollResponse = client.deleteByQuery(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 批量删除
     *
     * @param idxName index
     * @param idList  待删除列表
     * @return void
     * @throws
     * @since
     */
    public <T> void deleteBatch(String idxName, Collection<T> idList) {
        BulkRequest request = new BulkRequest();
        idList.forEach(item -> request.add(new DeleteRequest(idxName, item.toString())));
        try {
            client.bulk(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteDocById(String index, String id) {
        try {
            DeleteRequest request = new DeleteRequest(index, id);
            client.delete(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
