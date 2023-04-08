package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

@Api("Запрос на создание пользователя")
public class LoginUserRequest {
    @ApiModelProperty(value = "Имя пользователя")
    private final String username;
    @ApiModelProperty(value = "Пароль пользователя")
    private final String password;

    @JsonCreator
    public LoginUserRequest(@JsonProperty("username") String username, @JsonProperty("password") String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
