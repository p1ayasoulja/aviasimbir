package com.example.aviasimbir.service;

import com.example.aviasimbir.Jwt.JwtTokenProvider;
import com.example.aviasimbir.entity.User;
import com.example.aviasimbir.exceptions.UserWasNotFoundException;
import com.example.aviasimbir.exceptions.WrongArgumentException;
import com.example.aviasimbir.requestresponse.LoginUserRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(AuthenticationManager authenticationManager, UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Аутентификация пользователя
     *
     * @param loginUserRequest данные пользователя
     * @return токен пользователя
     * @throws UserWasNotFoundException ошибка данных пользователя
     * @throws WrongArgumentException   ошибка неверно введенных данных
     */
    public String loginUser(LoginUserRequest loginUserRequest) throws UserWasNotFoundException, WrongArgumentException {
        if (loginUserRequest.getUsername() == null || loginUserRequest.getUsername().trim().isEmpty() ||
                loginUserRequest.getPassword() == null || loginUserRequest.getPassword().trim().isEmpty()) {
            throw new WrongArgumentException("Username and password can not be null or empty");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserRequest.getUsername(), loginUserRequest.getPassword()));
        User user = userService.findByUsername(loginUserRequest.getUsername()).orElseThrow();
        return jwtTokenProvider.createToken(user.getUsername(), user.getRole());
    }

}
