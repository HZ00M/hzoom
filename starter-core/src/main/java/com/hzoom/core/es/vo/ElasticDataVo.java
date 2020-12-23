package com.hzoom.core.es.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ElasticDataVo
 * @Description http交互Vo对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElasticDataVo<T> {

    /**
     * 索引名
     */
    private String index;
    /**
     * 数据存储对象
     */
    private ElasticEntity<T> elasticEntity;

}