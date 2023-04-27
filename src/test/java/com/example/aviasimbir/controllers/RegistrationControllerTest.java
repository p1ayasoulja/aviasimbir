package com.example.aviasimbir.controllers;

import com.example.aviasimbir.entity.Role;
import com.example.aviasimbir.entity.User;
import com.example.aviasimbir.exceptions.RegisterUserException;
import com.example.aviasimbir.exceptions.UnavailableUsernameException;
import com.example.aviasimbir.requestresponse.RegisterUserRequest;
import com.example.aviasimbir.requestresponse.RegisterUserResponse;
import com.example.aviasimbir.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class RegistrationControllerTest {
    private final UserService userService = Mockito.mock(UserService.class);
    private final RegistrationController registrationController = new RegistrationController(userService);

    @Test
    public void testRegisterUserSuccess() throws RegisterUserException, UnavailableUsernameException {
        RegisterUserRequest request = new RegisterUserRequest("testName", "testPass");
        User user = new User("testName", "testPass", Role.REPRESENTATIVE);
        when(userService.register(request.getUsername(), request.getPassword())).thenReturn(user);

        ResponseEntity<RegisterUserResponse> response = registrationController.registerUser(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user.getId(), Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    //todo
    public void testRegisterUserInvalidUsername() throws RegisterUserException, UnavailableUsernameException {
        RegisterUserRequest request = new RegisterUserRequest("", "password123");
        when(userService.register(request.getUsername(), request.getPassword())).thenThrow(new RegisterUserException("Username and password cannot be null or empty"));

        assertThrows(RegisterUserException.class, () -> registrationController.registerUser(request));
    }

    @Test
    public void testRegisterUserUnavailableUsername() throws RegisterUserException, UnavailableUsernameException {
        RegisterUserRequest request = new RegisterUserRequest("john_doe", "password123");
        when(userService.register(request.getUsername(), request.getPassword())).thenThrow(new UnavailableUsernameException("User with this username already exists"));

        assertThrows(UnavailableUsernameException.class, () -> registrationController.registerUser(request));
    }

}