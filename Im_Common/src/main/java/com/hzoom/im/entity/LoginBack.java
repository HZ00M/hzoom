package com.hzoom.im.entity;

import com.hzoom.im.bean.UserDTO;
import lombok.Data;

@Data
public class LoginBack {

    ImNode imNode;

    private String token;

    private UserDTO userDTO;

}
