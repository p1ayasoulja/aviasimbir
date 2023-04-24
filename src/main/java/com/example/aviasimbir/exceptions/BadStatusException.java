package com.example.aviasimbir.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadStatusException extends Exception {
    public BadStatusException(String message) {
        super(message);
    }
}