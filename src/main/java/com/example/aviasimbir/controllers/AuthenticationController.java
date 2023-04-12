package com.example.aviasimbir.controllers;

import com.example.aviasimbir.Jwt.JwtTokenProvider;
import com.example.aviasimbir.entity.User;
import com.example.aviasimbir.exceptions.UserWasNotFoundException;
import com.example.aviasimbir.requestresponse.LoginUserRequest;
import com.example.aviasimbir.requestresponse.LoginUserResponse;
import com.example.aviasimbir.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Autowired
    AuthenticationController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation("Аутентификация пользователя")
    public ResponseEntity<LoginUserResponse> auth(@RequestBody LoginUserRequest loginUserRequest) throws UserWasNotFoundException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserRequest.getUsername(), loginUserRequest.getPassword()));
        User user = userService.findByUsername(loginUserRequest.getUsername()).get();
        String token = jwtTokenProvider.createToken(user.getUsername(), user.getRole());
        return ResponseEntity.ok(new LoginUserResponse(token));
    }
}
