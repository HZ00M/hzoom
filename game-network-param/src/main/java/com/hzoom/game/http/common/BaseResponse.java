package com.hzoom.game.http.common;

import com.alibaba.fastjson.JSONObject;
import com.hzoom.game.error.IError;
import lombok.Data;

@Data
public class BaseResponse<T> {
    private int code;           //返回的消息码，如果消息正常返回，code == 0，否则返回错误码
    private T data;             //消息实体
    private String errorMsg;    //当code != 0时，这里表示错误的详细信息

    public BaseResponse() {
    }

    public BaseResponse(IError error) {
        super();
        this.code = error.getErrorCode();
        this.errorMsg = error.getErrorDesc();
    }

    public BaseResponse(T data) {
        super();
        this.data = data;
    }

    public static <T> BaseResponse<T> parseObject(String response,Class<T> t){
        JSONObject root = JSONObject.parseObject(response);
        int code = root.getIntValue("code");
        BaseResponse<T> result = new BaseResponse<>();
        if (code ==0){
            JSONObject dataJson = root.getJSONObject("data");
            T data = dataJson.toJavaObject(t);
            result.setData(data);
        }else {
            String errorMsg = root.getString("errorMsg");
            result.setCode(code);
            result.setErrorMsg(errorMsg);
        }
        return result;
    }

}
