package com.example.core.es.vo;

import lombok.Data;

@Data
public class ElasticEntity<T> {
    private String id;
    private T data;
}
