package com.example.aviasimbir.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserWasNotFoundException extends Exception {
    public UserWasNotFoundException(String message) {
        super(message);
    }
}
