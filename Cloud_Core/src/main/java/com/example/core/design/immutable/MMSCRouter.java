package com.example.core.design.immutable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 路由管理器
 * 模式角色  ImmutableObject
 * 1 使用finnal定义类，防止被子类修改行为
 * 2 所有字段采用finnal修饰，保证对象的初始化安全，当finnal字段被其他类可见时，他必定是初始化完成的
 */
public final class MMSCRouter {
    //实例用volatile修饰，保证多线程环境该变量可见性
    private static volatile MMSCRouter instance = new MMSCRouter();
    private final Map<String, MMSCInfo> routerMap;

    public MMSCRouter() {
        this.routerMap = MMSCRouter.RouteFromDB();
    }

    private static Map RouteFromDB() {
        Map<String, MMSCInfo> map = new HashMap<String, MMSCInfo>();
        return map;
    }

    public MMSCRouter getInstance() {
        return instance;
    }

    public MMSCInfo getMMSC(String prefix) {
        return routerMap.get(prefix);
    }

    /**
     * 更新新实例对象
     */
    public static void setInstance(MMSCRouter newInstance) {
        instance = newInstance;
    }

    /**
     * 深度拷贝
     *
     * @return
     */
    private static Map<String, MMSCInfo> deepcopy(Map<String, MMSCInfo> m) {
        Map<String, MMSCInfo> result = new HashMap<String, MMSCInfo>();
        for (String key : m.keySet()) {
            result.put(key, new MMSCInfo(m.get(key)));
        }
        return result;
    }

    /**
     * 防御性复制
     */
    public Map<String, MMSCInfo> getRouterMap() {
        return Collections.unmodifiableMap(deepcopy(routerMap));
    }
}

/**
 * 彩信信息实体
 */
final class MMSCInfo {
    private final String deviceID;
    private final String url;

    public MMSCInfo(String deviceID, String url, CopyOnWriteArrayList copyOnWriteArrayList) {
        this.deviceID = deviceID;
        this.url = url;
    }

    public MMSCInfo(MMSCInfo mmscInfo) {
        this.deviceID = mmscInfo.deviceID;
        this.url = mmscInfo.url;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public String getUrl() {
        return url;
    }
}
