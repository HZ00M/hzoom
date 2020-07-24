package com.example.demo.domain;

import com.example.core.sqlgen.Id;
import com.example.core.sqlgen.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_user")
public class User implements Serializable {
    private Integer id;

    private String username;

    private String password;

    @Id("nick_name")
    private String nickName;

}
