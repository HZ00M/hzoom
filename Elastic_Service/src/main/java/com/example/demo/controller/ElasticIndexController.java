package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.example.core.es.config.BaseElasticService;
import com.example.core.es.vo.IdxVo;
import com.example.demo.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName ElasticIndexController
 * @Description ElasticSearch索引的基本管理，提供对外查询、删除和新增功能
 */
@Slf4j
@RequestMapping("/elastic")
@RestController
public class ElasticIndexController {

    @Autowired
    BaseElasticService baseElasticService;

    @GetMapping(value = "/")
    public Result index(String index){
        return new Result();
    }

    /**
     * @Description 创建Elastic索引
     * @param idxVo
     * @return xyz.wongs.weathertop.base.message.response.Result
     * @throws
     * @date 2019/11/19 11:07
     */
    @PostMapping(value = "/createIndex")
    public Result createIndex(@RequestBody IdxVo idxVo){
        try {
            //索引不存在，再创建，否则不允许创建
            if(!baseElasticService.isExistsIndex(idxVo.getIdxName())){
                String idxSql = JSON.toJSONString(idxVo.getIdxSql());
                log.warn(" idxName={}, idxSql={}",idxVo.getIdxName(),idxSql);
                baseElasticService.createIndex(idxVo.getIdxName(),idxSql);
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
     * @return xyz.wongs.weathertop.base.message.response.Result
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
}