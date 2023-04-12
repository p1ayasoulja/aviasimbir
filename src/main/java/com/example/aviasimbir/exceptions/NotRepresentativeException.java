package com.example.aviasimbir.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class NotRepresentativeException extends Exception {
    public NotRepresentativeException(String message) {
        super(message);
    }
}
