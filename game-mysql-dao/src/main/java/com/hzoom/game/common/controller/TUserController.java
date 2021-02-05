package com.hzoom.game.common.controller;


import com.hzoom.game.common.entity.TUser;
import com.hzoom.game.common.service.TUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author hzoom
 * @since 2021-02-04
 */
@RestController
@RequestMapping("/tUser")
public class TUserController {
    @Autowired
    private TUserService userService;

    @RequestMapping
    public List<TUser> test(){
        List<TUser> list = userService.list();
        return list;
    }
}
