package com.hzoom.im.feign;

import feign.Param;
import feign.RequestLine;

public interface UserController {
    @RequestLine("GET /user/login/{username}/{password}")
    String loginAction(@Param("username") String username, @Param("password") String password);

    @RequestLine("GET /{userid}")
    String getById(@Param("userid") Integer userid);
}
