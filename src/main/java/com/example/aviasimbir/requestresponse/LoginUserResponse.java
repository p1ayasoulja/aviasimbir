package com.example.aviasimbir.requestresponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

@Api("Ответ на создание пользователся")
public class LoginUserResponse {
    @ApiModelProperty(value = "Токен")
    private final String token;

    public LoginUserResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }


}
