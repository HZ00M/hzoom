package com.hzoom.im.feign;

import com.hzoom.im.constants.ServerConstants;
import com.hzoom.im.entity.LoginBack;
import com.hzoom.im.utils.JsonUtil;
import feign.Feign;
import feign.codec.StringDecoder;

public class WebOperator {
    public static LoginBack login(String userName, String password) {
        UserController action = Feign.builder()
//                .decoder(new GsonDecoder())
                .decoder(new StringDecoder())
                .target(UserController.class, ServerConstants.WEB_URL);

        String s = action.loginAction(userName, password);

        LoginBack back = JsonUtil.jsonToPojo(s, LoginBack.class);
        return back;
    }
}
