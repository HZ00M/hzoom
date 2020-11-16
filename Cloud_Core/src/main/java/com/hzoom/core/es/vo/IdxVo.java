package com.hzoom.core.es.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdxVo {

    private String index;
    private Mapping mappings;
    private Map<String,Integer> settings;



    @Data
    public static class Mapping {

        private boolean dynamic=false;
        private Map<String, Map<String, Object>> properties;

    }
}
