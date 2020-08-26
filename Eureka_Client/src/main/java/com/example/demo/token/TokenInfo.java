package com.example.demo.token;

import lombok.Data;

@Data
public class TokenInfo {
    /**
     * token类型: api:0 、user:1
     */
    private Integer tokenType;
    /**
     * App 信息
     */
    private AppInfo appInfo;
    /**
     * 用户其他数据
     */
    private UserInfo userInfo;
}
