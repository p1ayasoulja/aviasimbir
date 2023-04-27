package com.example.aviasimbir.controllers;

import com.example.aviasimbir.exceptions.UserWasNotFoundException;
import com.example.aviasimbir.exceptions.WrongArgumentException;
import com.example.aviasimbir.requestresponse.LoginUserRequest;
import com.example.aviasimbir.requestresponse.LoginUserResponse;
import com.example.aviasimbir.service.AuthService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
public class AuthenticationController {

    private final AuthService authService;

    public AuthenticationController(AuthService authService) {
        this.authService = authService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation("Аутентификация пользователя")
    public ResponseEntity<LoginUserResponse> auth(@RequestBody LoginUserRequest loginUserRequest) throws UserWasNotFoundException, WrongArgumentException {
        String token = authService.loginUser(loginUserRequest);
        LoginUserResponse loginUserResponse = new LoginUserResponse(token);
        return ResponseEntity.ok(loginUserResponse);
    }
}
