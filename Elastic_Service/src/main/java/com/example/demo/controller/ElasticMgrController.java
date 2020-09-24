package com.example.demo.controller;

import com.example.core.es.config.BaseElasticService;
import com.example.core.es.utils.ElasticUtil;
import com.example.core.es.vo.ElasticDataVo;
import com.example.core.es.vo.ElasticEntity;
import com.example.core.es.vo.QueryVo;
import com.example.demo.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 数据管理
 * @ClassName ElasticMgrController
 * @Description
 * @author WCNGS@QQ.COM
 * @Github <a>https://github.com/rothschil</a>
 * @date 2019/10/25 16:55
 * @Version 1.0.0
 */
@Slf4j
@RequestMapping("/elasticMgr")
@RestController
public class ElasticMgrController {

    @Autowired
    private BaseElasticService baseElasticService;



    /**
     * @Description 新增数据
     * @param elasticDataVo
     * @throws
     */
    @PostMapping(value = "/add")
    public Result add(@RequestBody ElasticDataVo elasticDataVo){
        Result result = new Result();
        try {
            if(!StringUtils.isNotEmpty(elasticDataVo.getIdxName())){
                log.warn("索引为空");
                return result.error("索引为空");
            }
            ElasticEntity elasticEntity = new ElasticEntity();
            elasticEntity.setId(elasticDataVo.getElasticEntity().getId());
            elasticEntity.setData(elasticDataVo.getElasticEntity().getData());

            baseElasticService.insertOrUpdateOne(elasticDataVo.getIdxName(), elasticEntity);
        } catch (Exception e) {
            log.error("插入数据异常，metadataVo={},异常信息={}", elasticDataVo.toString(),e.getMessage());
        }
        return result.success();
    }


    /**
     * @Description 删除
     * @param elasticDataVo
     * @throws
     */
    @PostMapping(value = "/delete")
    public Result delete(@RequestBody ElasticDataVo elasticDataVo){
        try {
            if(!StringUtils.isNotEmpty(elasticDataVo.getIdxName())){
                log.warn("索引为空");
                return Result.error("索引为空");
            }
            baseElasticService.deleteIndex(elasticDataVo.getIdxName());
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
    @GetMapping(value = "/get")
    public Result get(@RequestBody QueryVo queryVo){

        if(!StringUtils.isNotEmpty(queryVo.getIdxName())){
            log.warn("索引为空");
            return Result.error("索引为空");
        }

        try {
            Class<?> clazz = ElasticUtil.getClazz(queryVo.getClassName());
            Map<String,Object> params = queryVo.getQuery().get("match");
            Set<String> keys = params.keySet();
            MatchQueryBuilder queryBuilders=null;
            for(String ke:keys){
                queryBuilders = QueryBuilders.matchQuery(ke, params.get(ke));
            }
            if(null!=queryBuilders){
                SearchSourceBuilder searchSourceBuilder = ElasticUtil.initSearchSourceBuilder(queryBuilders);
                List<?> data = baseElasticService.search(queryVo.getIdxName(),searchSourceBuilder,clazz);
                return new Result().success(data);
            }
        } catch (Exception e) {
            log.error("查询数据异常，metadataVo={},异常信息={}", queryVo.toString(),e.getMessage());
            Result.error("服务忙，请稍后再试");
        }
        return Result.success();
    }
}
