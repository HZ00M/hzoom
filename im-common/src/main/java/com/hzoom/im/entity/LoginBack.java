package com.hzoom.im.entity;

import com.hzoom.im.bean.UserDTO;
import lombok.Data;

import java.io.Serializable;

@Data
public class LoginBack implements Serializable {


    ImNode imNode;

    private String token;

    private UserDTO userDTO;

}
