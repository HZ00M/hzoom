package com.hzoom.demo.token;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppInfo {
    /**
     * App id
     */
    private String appId;
    /**
     * API 秘钥
     */
    private String key;
}
