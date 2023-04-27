package com.example.aviasimbir.controllers;

import com.example.aviasimbir.exceptions.UserWasNotFoundException;
import com.example.aviasimbir.exceptions.WrongArgumentException;
import com.example.aviasimbir.requestresponse.LoginUserRequest;
import com.example.aviasimbir.requestresponse.LoginUserResponse;
import com.example.aviasimbir.service.AuthService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
class AuthenticationControllerTest {
    private final AuthService authService = Mockito.mock(AuthService.class);
    private final AuthenticationController authenticationController = new AuthenticationController(authService);

    @Test
    public void testAuth() throws UserWasNotFoundException, WrongArgumentException {
        LoginUserRequest request = new LoginUserRequest("test_username", "test_password");
        String expectedToken = "test_token";
        Mockito.when(authService.loginUser(request)).thenReturn(expectedToken);
        ResponseEntity<LoginUserResponse> response = authenticationController.auth(request);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(expectedToken, Objects.requireNonNull(response.getBody()).getToken());
    }

    @Test
    public void testAuthInvalidCredentials() throws WrongArgumentException, UserWasNotFoundException {
        Mockito.when(authService.loginUser(Mockito.any(LoginUserRequest.class))).thenThrow(new UserWasNotFoundException("User was not found"));
        LoginUserRequest request = new LoginUserRequest("invalid_username", "invalid_password");
        UserWasNotFoundException exception = Assertions.assertThrows(UserWasNotFoundException.class, () -> authenticationController.auth(request));
        Assertions.assertEquals("User was not found", exception.getMessage());
    }

    @Test
    public void testAuthNullFields() throws WrongArgumentException, UserWasNotFoundException {
        Mockito.when(authService.loginUser(Mockito.any(LoginUserRequest.class))).thenThrow(new WrongArgumentException("Username and password can not be null or empty"));
        LoginUserRequest request = new LoginUserRequest("", null);
        WrongArgumentException exception = Assertions.assertThrows(WrongArgumentException.class, () -> authenticationController.auth(request));
        Assertions.assertEquals("Username and password can not be null or empty", exception.getMessage());
    }


}