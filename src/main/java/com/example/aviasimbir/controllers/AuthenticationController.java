package com.example.aviasimbir.controllers;

import com.example.aviasimbir.Jwt.JwtTokenProvider;
import com.example.aviasimbir.entity.User;
import com.example.aviasimbir.requestresponse.LoginUserRequest;
import com.example.aviasimbir.requestresponse.LoginUserResponse;
import com.example.aviasimbir.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

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
    @ApiOperation("Авторизация пользователя")
    public ResponseEntity<LoginUserResponse> login(@RequestBody LoginUserRequest loginUserRequest) {
        try {
            Optional<User> userOpt = userService.findByUsername(loginUserRequest.getUsername());
            if (userOpt.isEmpty()) {
                throw new UsernameNotFoundException("User with username : " + loginUserRequest.getUsername() + " not found");
            }
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUserRequest.getUsername(), loginUserRequest.getPassword()));
            User user = userOpt.get();
            String token = jwtTokenProvider.createToken(user.getUsername(), user.getRole());
            LoginUserResponse loginUserResponse = new LoginUserResponse(token);
            return ResponseEntity.ok(loginUserResponse);

        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }

    }
}
