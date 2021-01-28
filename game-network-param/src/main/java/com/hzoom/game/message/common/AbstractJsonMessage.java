package com.hzoom.game.message.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public abstract class AbstractJsonMessage<T> extends AbstractMessage {
    private T bodyObj;//具体的参数类实例对象。所有的请求参数和响应参数，必须以对象的形式存在。

    public AbstractJsonMessage() {
        if (getBodyObjClass() != null){
            try {
                bodyObj = getBodyObjClass().newInstance();
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
                bodyObj = null;
            }

        }
    }

    protected abstract Class<T> getBodyObjClass();//由子类返回具体的参数对象类型。

    @Override
    protected byte[] encode(){
        String jsonString = JSON.toJSONString(bodyObj);
        return jsonString.getBytes();
    }

    @Override
    protected void decode(byte[] body){
        String jsonString = new String(body);
        bodyObj = JSONObject.parseObject(body,getBodyObjClass());
    }

    @Override
    protected boolean isNullBody(){
        return bodyObj == null;
    }

    public T getBodyObj() {
        return bodyObj;
    }

    public void setBodyObj(T bodyObj) {
        this.bodyObj = bodyObj;
    }

    @Override
    public String toString() {
        String msg = null;
        if (this.bodyObj != null) {
            msg = JSON.toJSONString(bodyObj);
        }
        return "Header:" + this.getHeader() + ", " + this.getClass().getSimpleName() + "=[bodyObj=" + msg + "]";
    }
}
