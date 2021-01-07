package com.hzoom.game.http.response;

import lombok.Data;

@Data
public class LoginResponse {
    private long userId;
    private String token;
}
