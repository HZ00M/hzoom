package com.hzoom.im.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "im-web")
public interface UserController {
    @GetMapping("/user/login")
    String loginAction(@RequestParam("username") String username, @RequestParam("password") String password);

}
