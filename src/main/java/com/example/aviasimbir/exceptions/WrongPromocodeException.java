package com.example.aviasimbir.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class WrongPromocodeException extends Exception {
    public WrongPromocodeException(String message) {
        super(message);
    }
}
