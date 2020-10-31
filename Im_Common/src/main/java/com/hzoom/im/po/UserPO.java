package com.hzoom.im.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
public class UserPO implements Serializable {
    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 注册时间
     */
    private Date registerTime;

    /**
     * 密码
     */
    private String passWord;


}
