package com.hzoom.demo.token;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Data
@Slf4j
public class ApiResponse<T> {
    /**
     * 结果
     */
    private ApiResult result;
    /**
     * 数据
     */
    private T data;
    /**
     * 签名
     * sign 简介
     *
     * nonce：随机值，是客户端随机生成的值，作为参数传递过来，随机值的目的是增加sign签名的多变性。随机值一般是数字和字母的组合，6位长度，随机值的组成和长度没有固定规则。
     *
     * sign: 一般用于参数签名，防止参数被非法篡改，最常见的是修改金额等重要敏感参数， sign的值一般是将所有非空参数按照升续排序然后+token+key+timestamp+nonce(随机数)拼接在一起，
     * 然后使用某种加密算法进行加密，作为接口中的一个参数sign来传递，也可以将sign放到请求头中。接口在网络传输过程中如果被黑客挟持，并修改其中的参数值，然后再继续调用接口，
     * 虽然参数的值被修改了，但是因为黑客不知道sign是如何计算出来的，不知道sign都有哪些值构成，不知道以怎样的顺序拼接在一起的，最重要的是不知道签名字符串中的key是什么，
     * 所以黑客可以篡改参数的值，但没法修改sign的值，当服务器调用接口前会按照sign的规则重新计算出sign的值然后和接口传递的sign参数的值做比较，如果相等表示参数值没有被篡改，
     * 如果不等，表示参数被非法篡改了，就不执行接口了。
     */
    private String sign;

    public static <T> ApiResponse success(T data) {
        return response(ApiCodeEnum.SUCCESS.getCode(), ApiCodeEnum.SUCCESS.getMsg(), data);
    }

    public static ApiResponse error(String code, String msg) {
        return response(code, msg, null);
    }

    public static <T> ApiResponse response(String code, String msg, T data) {
        ApiResult result = new ApiResult(code, msg);
        ApiResponse response = new ApiResponse();
        response.setResult(result);
        response.setData(data);
        String sign = signData(data);
        response.setSign(sign);
        return response;
    }

    private static <T> String signData(T data) {
// TODO 查询key
        String key = "12345678954556";
        Map<String, String> responseMap = null;
        try {
            responseMap = getFields(data);
        } catch (IllegalAccessException e) {
            return null;
        }
        String urlComponent = ApiUtil.concatSignString(responseMap);
        String signature = urlComponent + "key=" + key;
        String sign = MD5Util.encode(signature);
        return sign;
    }

    /**
     * @param data 反射的对象,获取对象的字段名和值
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static Map<String, String> getFields(Object data) throws IllegalAccessException, IllegalArgumentException {
        if (data == null) return null;
        Map<String, String> map = new HashMap<>();
        Field[] fields = data.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            String name = field.getName();
            Object value = field.get(data);
            if (field.get(data) != null) {
                map.put(name, value.toString());
            }
        }
        return map;
    }
}
