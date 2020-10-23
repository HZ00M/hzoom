package com.example.demo.po;

import com.example.core.sqlgen.annotation.Id;
import com.example.core.sqlgen.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_user")
public class User implements Serializable {
    @Id("id")
    private Integer id;

    private String username;

    private String password;

    private String nickName;

    public User(Integer id, String username) {
        this.id = id;
        this.username = username;
    }
}
