package com.hzoom.demo.controller;

import com.alibaba.fastjson.JSON;
import com.hzoom.core.es.config.BaseElasticService;
import com.hzoom.core.es.utils.ElasticUtil;
import com.hzoom.core.es.vo.ElasticDataVo;
import com.hzoom.core.es.vo.IdxVo;
import com.hzoom.core.es.vo.QueryVo;
import com.hzoom.demo.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.HistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
    @RequestMapping("/elastic")
@RestController
public class ElasticController {

    @Autowired
    BaseElasticService baseElasticService;

    @GetMapping(value = "/")
    public Result index(String index){
        return new Result();
    }

    /**
     {
       "index" : "indexName",
       "settings": {
         "number_of_shards": 2,
         "number_of_replicas": 0
       } ,
        "mappings": {
              "properties": {
               "name": {
                 "type": "text",
                 "analyzer": "ik_max_word"
               },
               "info":{
                 "type":"text",
                 "analyzer":"ik_max_word"
               },
               "price":{
                 "type":"float"
               },
               "image":{
                 "type":"keyword",
                 "index":false
               }
             }
       }
     }
     */
    @PostMapping(value = "/createIndex")
    public Result createIndex(@RequestBody IdxVo idxVo){
        try {
            //索引不存在，再创建，否则不允许创建
            if(!baseElasticService.isExistsIndex(idxVo.getIndex())){
                String mapping = JSON.toJSONString(idxVo.getMappings());
                log.warn(" idxName={}, idxSql={}",idxVo.getIndex(),mapping);
                String setting = JSON.toJSONString(idxVo.getSettings());
                baseElasticService.createIndex(idxVo.getIndex(),mapping,setting);
            } else{
                Result.error("索引已经存在，不允许创建");
            }
        } catch (Exception e) {
            Result.error("索引已经存在，不允许创建");
        }
        return Result.success();
    }


    /**
     * @Description 判断索引是否存在；存在-TRUE，否则-FALSE
     * @param index
     * @throws
     * @date 2019/11/19 18:48
     */
    @GetMapping(value = "/exist/{index}")
    public Result indexExist(@PathVariable(value = "index") String index){

        try {
            if(!baseElasticService.isExistsIndex(index)){
                log.error("index={},不存在",index);
                Result.success();
            } else {
                Result.error(" 索引已经存在" );
            }
        } catch (Exception e) {
            Result.error(" 调用ElasticSearch 失败" );
        }
        return Result.success();
    }

    @GetMapping(value = "/del/{index}")
    public Result indexDel(@PathVariable(value = "index") String index){
        try {
            baseElasticService.deleteIndex(index);
        } catch (Exception e) {
            Result.error(" 调用ElasticSearch 失败" );
        }
        return Result.success();
    }


    /**
     * @Description 新增数据
     * @param elasticDataVo
     * @throws
     */
    @PostMapping(value = "/add")
    public Result add(@RequestBody ElasticDataVo elasticDataVo){
        try {
            if(!StringUtils.isNotEmpty(elasticDataVo.getIndex())){
                log.warn("索引为空");
                return Result.error("索引为空");
            }

            baseElasticService.insertOrUpdateOne(elasticDataVo.getIndex(), elasticDataVo.getElasticEntity());
        } catch (Exception e) {
            log.error("插入数据异常，metadataVo={},异常信息={}", elasticDataVo.toString(),e.getMessage());
        }
        return Result.success();
    }


    /**
     * @Description 删除
     * @param elasticDataVo
     * @throws
     */
    @PostMapping(value = "/delete")
    public Result delete(@RequestBody ElasticDataVo elasticDataVo){
        try {
            if(!StringUtils.isNotEmpty(elasticDataVo.getIndex())){
                log.warn("索引为空");
                return Result.error("索引为空");
            }
            baseElasticService.deleteIndex(elasticDataVo.getIndex());
        } catch (Exception e) {
            log.error("删除数据失败");
        }
        return Result.success();

    }



    /**
     * @Description
     * @param queryVo 查询实体对象
     * @throws
     */
    @RequestMapping(value = "/get")
    public Result get(@RequestBody QueryVo queryVo){

        if(!StringUtils.isNotEmpty(queryVo.getIndex())){
            log.warn("索引为空");
            return Result.error("索引为空");
        }

        try {
            Class<?> clazz = ElasticUtil.getClazz(queryVo.getClassName());
            Map<String,Object> params = queryVo.getQuery().get("match");
            Set<String> keys = params.keySet();
            MatchQueryBuilder queryBuilders=null;
            for(String key:keys){
                queryBuilders = QueryBuilders.matchQuery(key, params.get(key));
            }
            if(null!=queryBuilders){
                SearchSourceBuilder searchSourceBuilder = ElasticUtil.initSearchSourceBuilder(queryBuilders);
                List<?> data = baseElasticService.query(queryVo.getIndex(),searchSourceBuilder,clazz);
                return new Result().success(data);
            }
        } catch (Exception e) {
            log.error("查询数据异常，metadataVo={},异常信息={}", queryVo.toString(),e.getMessage());
            Result.error("服务忙，请稍后再试");
        }
        return Result.success();
    }

    /**
     * query DSL
     * match match_phrase  以什么字符开头 wildcards查询：通配符查询 regexp 查询：正则表达式查询
     *
     * filter DSL
     * term 过滤：精确匹配 terms 过滤：指定多个匹配条件 range 过滤 exists/missing 过滤：过滤字段是否存在 bool过滤：合并多个过滤条件查询结果的布尔逻辑
     */

    /**
     * 根据id查询
     */
    @RequestMapping(value = "/queryById/{index}/{id}")
    public Result queryById(@PathVariable String index,@PathVariable String id){
        QueryBuilder queryBuilder = QueryBuilders.idsQuery().addIds(id);
        return new Result().success(baseElasticService.queryDoc(index,queryBuilder));
    }

    /**
     * 查询所有
     * @param index
     * @return
     */
    @RequestMapping(value = "/queryAll/{index}")
    public Result queryAll(@PathVariable String index){
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        return new Result().success(baseElasticService.queryDoc(index,queryBuilder));
    }

    /**
     * 多字段匹配
     */
    @RequestMapping(value = "/termQeuryField/{index}")
    public Result termQeuryField(@PathVariable String index){
        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery("手机","name","info") ;
        return  new Result().success(baseElasticService.queryDetailDoc(index,queryBuilder));
    }
    /**
     * 多值匹配
     */
    @RequestMapping(value = "/termQeuryValue/{index}")
    public Result termQeuryValue(@PathVariable String index){
        QueryBuilder queryBuilder1 = QueryBuilders.termsQuery("name","华为","电视");
        QueryBuilder queryBuilder2 = QueryBuilders.termsQuery("name",new ArrayList<String>().add("手机"));
        return  new Result().success(baseElasticService.queryDetailDoc(index,queryBuilder1));
    }

    /**
     * 布尔查询
     bool 查询
     must: 查询指定文档一定要被包含。
     filter: 和must类似，但不计分。
     must_not: 查询指定文档一定不要被包含。
     should: 查询指定文档，满足一个条件就返回。
     * 组合查询
     * term 过滤：精确匹配
     * must(QueryBuilders) :   AND
     * mustNot(QueryBuilders): NOT
     * should:                  : OR
     */
    @RequestMapping(value = "/boolQueryTest/{index}")
    public Result boolQueryTest(@PathVariable String index){
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("name","include_name"))
                .mustNot(QueryBuilders.termQuery("name","exclude_name"))
                .mustNot(QueryBuilders.rangeQuery("price").from("1").to("12").includeUpper(true).includeLower(false));
        return new Result().success(baseElasticService.queryDetailDoc(index,queryBuilder));
    }

    /**
     * 包裹查询, 高于设定分数, 不计算相关性
     */
    @RequestMapping(value = "/testConstantScoreQuery/{index}")
    public Result testConstantScoreQuery(@PathVariable String index) {
        QueryBuilder queryBuilder = QueryBuilders.constantScoreQuery(QueryBuilders.termQuery("name", "华为")).boost(2.0f);
        return new Result().success(baseElasticService.queryDetailDoc(index,queryBuilder));
    }

    /**
     * disMax查询
     * 对子查询的结果做union, score沿用子查询score的最大值,
     * boost权重
     * 广泛用于muti-field查询
     * 使用tie_breaker将其他query的分数也考虑进去
     */
    @RequestMapping(value = "/testDisMaxQuery/{index}")
    public Result testDisMaxQuery(@PathVariable String index) {
        QueryBuilder queryBuilder = QueryBuilders.disMaxQuery()
                .add(QueryBuilders.termQuery("name", "手机"))
                .add(QueryBuilders.termQuery("info", "品牌"))
                .boost(1.3f)
                .tieBreaker(0.7f);
        return new Result().success(baseElasticService.queryDetailDoc(index,queryBuilder));
    }
    /**
     * 嵌套查询, 内嵌文档查询
     */
    @RequestMapping(value = "/testNestedQuery/{index}")
    public Result testNestedQuery(@PathVariable String index) {
        QueryBuilder queryBuilder = QueryBuilders.nestedQuery("location",
                QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("location.lat", 0.962590433140581))
                        .must(QueryBuilders.rangeQuery("location.lon").lt(36.0000).gt(0.000)), ScoreMode.Avg);
        return new Result().success(baseElasticService.queryDetailDoc(index,queryBuilder));
    }

    /**
     * 通配符查询, 支持 *
     * 匹配任何字符序列, 包括空
     * 避免* 开始, 会检索大量内容造成效率缓慢
     */
    @RequestMapping(value = "/testWildCardQuery/{index}")
    public Result testWildCardQuery(@PathVariable String index) {
        QueryBuilder queryBuilder = QueryBuilders.wildcardQuery("info", "国*");
        return new Result().success(baseElasticService.queryDetailDoc(index,queryBuilder));
    }

    /**
     * 词条查询
     */
    @RequestMapping(value = "/testPhraseQuery/{index}")
    public Result testPhraseQuery(@PathVariable String index){
        QueryBuilder queryBuilder = QueryBuilders.matchPhraseQuery("info","品牌真是");
        return new Result().success(baseElasticService.queryDetailDoc(index,queryBuilder));
    }

    /**
     * prefix 查询
     */
    @RequestMapping(value = "/testPrefixQuery/{index}")
    public Result testPrefixQuery(@PathVariable String index){
        QueryBuilder queryBuilder = QueryBuilders.prefixQuery("info","国");
        return new Result().success(baseElasticService.queryDetailDoc(index,queryBuilder));
    }

    /**
     * agg 聚合查询  count max min avg sum
     */
    @RequestMapping(value = "/testAggQuery/{index}")
    public Result testAggQuery(@PathVariable String index){
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        AvgAggregationBuilder builder = AggregationBuilders.avg("price_avg").field("price");
        List list = new ArrayList();
        list.add(builder);
        return new Result().success(baseElasticService.queryDoc(index,queryBuilder,list));
    }

    /**
     *   Terms Aggregation桶 查询
     */
    @RequestMapping(value = "/testTermsAggregationQuery/{index}")
    public Result testTermsAggregationQuery(@PathVariable String index){
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        TermsAggregationBuilder builder = AggregationBuilders.terms("price_terms").field("price");
        List list = new ArrayList();
        list.add(builder);
        return new Result().success(baseElasticService.queryDoc(index,queryBuilder,list));
    }

    /**
     *   Terms Aggregation桶 查询
     */
    @RequestMapping(value = "/testHistogramAggregationQuery/{index}")
    public Result testHistogramAggregationQuery(@PathVariable String index){
        HistogramAggregationBuilder builder = AggregationBuilders.histogram("price_terms").field("price").interval(500);
        List list = new ArrayList();
        list.add(builder);
        return new Result().success(baseElasticService.queryDoc(index,null,list));
    }

}