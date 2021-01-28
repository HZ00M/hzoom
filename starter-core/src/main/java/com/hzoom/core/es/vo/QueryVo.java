package com.hzoom.core.es.vo;

import lombok.Data;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Data
public class QueryVo {

    /**
     * 索引名
     */
    private String index;
    /**
     * 需要反射的实体类型，用于对查询结果的封装
     */
    private String className = "java.lang.Object";
    /**
     * 查询类型
     */
    private QueryEnum queryType;
    /**
     * 具体条件
     */
    private Map<String, Map<String, Object>> query = new HashMap<>();

    /**
     * 查询配置
     *
     * @return
     */
    private Map<String, Object> config = new HashMap<>();

    public SearchRequest buildRequest() {
        SearchRequest request = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = buildSearchRequest();
        QueryBuilder queryBuilder = null;
        switch (queryType) {
            case matchAllQuery:
                /**
                 * 查询所有
                 */
                queryBuilder = QueryBuilders.matchAllQuery();
                break;
            case idsQuery:
                /**
                 * 根据id查询
                 */
                queryBuilder = QueryBuilders.idsQuery();
                break;
            case multiMatchQuery:
                /**
                 * 多字段匹配
                 */
                for (String text : query.keySet()) {
                    Map<String, Object> subMap = query.get(text);
                    ArrayList<String> names = new ArrayList<>(subMap.size());
                    for (Map.Entry entry : subMap.entrySet()) {
                        names.add((String) entry.getValue());
                    }
                    queryBuilder = QueryBuilders.multiMatchQuery(text, names.stream().collect(Collectors.joining(",")));
                }
                break;
            case termsQuery:
                /**
                 * 多值匹配
                 */
                for (String text : query.keySet()) {
                    Map<String, Object> subMap = query.get(text);
                    ArrayList<String> names = new ArrayList<>(subMap.size());
                    for (Map.Entry entry : subMap.entrySet()) {
                        names.add((String) entry.getValue());
                    }
                    queryBuilder = QueryBuilders.termQuery(text, names.stream().collect(Collectors.joining(",")));
                }
                break;
            case boolQuery:
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
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                for (String type : query.keySet()) {
                    Map<String, Object> subMap = query.get(type);
                    if (Option.must.toString().equals(type)){
                        if ((Boolean) subMap.getOrDefault("range",false)){
                            boolQueryBuilder.must(
                                    QueryBuilders.rangeQuery("name")
                                            .from(subMap.get("from"))
                                            .to(subMap.get("to"))
                                            .includeLower((Boolean) subMap.getOrDefault("includeLower",true))
                                            .includeUpper((Boolean) subMap.getOrDefault("includeUpper",true)));
                        }else {
                            boolQueryBuilder.must(QueryBuilders.termQuery((String) subMap.get("name"), subMap.get("value")));
                        }
                    }else if (Option.must.toString().equals("mustNot")){
                        if ((Boolean) subMap.getOrDefault("range",false)){
                            boolQueryBuilder.must(
                                    QueryBuilders.rangeQuery("name")
                                            .from(subMap.get("from"))
                                            .to(subMap.get("to"))
                                            .includeLower((Boolean) subMap.getOrDefault("includeLower",true))
                                            .includeUpper((Boolean) subMap.getOrDefault("includeUpper",true)));
                        }else {
                            boolQueryBuilder.mustNot(QueryBuilders.termQuery((String) subMap.get("name"), subMap.get("value")));
                        }

                    }
                }
                queryBuilder = boolQueryBuilder;
                break;
            case constantScoreQuery:
                /**
                 * 包裹查询, 高于设定分数, 不计算相关性
                 */
                ;
            case disMaxQuery:
                ;
            case nestedQuery:
                ;
            case wildcardQuery:
                ;
            case matchPhraseQuery:
                ;
            case prefixQuery:
                ;
            default:
                ;
        }
        sourceBuilder.query(queryBuilder);
        request.source(sourceBuilder);
        return request;
    }

    public SearchSourceBuilder buildSearchRequest() {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from((Integer) config.getOrDefault("from", -1));
        sourceBuilder.size((Integer) config.getOrDefault("size", -1));
        sourceBuilder.timeout(new TimeValue((Integer) config.getOrDefault("duration", 60), TimeUnit.SECONDS));
        return sourceBuilder;
    }


    public enum QueryEnum {
        matchAllQuery, idsQuery, multiMatchQuery, termsQuery, boolQuery, constantScoreQuery,
        disMaxQuery, nestedQuery, wildcardQuery, matchPhraseQuery, prefixQuery
    }

    public enum Option{
        must,mustNot,range
    }
}
