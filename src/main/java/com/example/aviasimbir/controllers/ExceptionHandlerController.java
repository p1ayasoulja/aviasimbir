package com.example.aviasimbir.controllers;

import com.example.aviasimbir.exceptions.*;
import com.example.aviasimbir.requestresponse.ExceptionResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerController {
    @ApiOperation("Обработка ошибки неверного идентификатора")
    @ExceptionHandler(NoSuchIdException.class)
    public ResponseEntity<ExceptionResponse> handleNoSuchIdException(NoSuchIdException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ApiOperation("Обработка ошибки неверных данных при создании/обновлении")
    @ExceptionHandler(WrongArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleWrongArgumentException(WrongArgumentException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ApiOperation("Обработка ошибки покупки/бронирования на уже отправленный рейс")
    @ExceptionHandler(PlaneAlreadyLeftException.class)
    public ResponseEntity<ExceptionResponse> handlePlaneAlreadyLeftException(PlaneAlreadyLeftException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ApiOperation("Обработка ошибки бронирования уже забронированного билета")
    @ExceptionHandler(TicketReservedException.class)
    public ResponseEntity<ExceptionResponse> handleTicketReservedException(TicketReservedException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ApiOperation("Обработка ошибки покупки уже проданного билета")
    @ExceptionHandler(TicketSoldException.class)
    public ResponseEntity<ExceptionResponse> handleTicketSoldException(TicketSoldException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ApiOperation("Обработка ошибки неверного времени прибытия(вылета) рейса")
    @ExceptionHandler(FlightTimeException.class)
    public ResponseEntity<ExceptionResponse> handleFlightTimeException(FlightTimeException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ApiOperation("Обработка ошибки неверных данных при регистрации")
    @ExceptionHandler(RegisterUserException.class)
    public ResponseEntity<ExceptionResponse> handleRegisterUserException(RegisterUserException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ApiOperation("Обработка ошибки неверного никнейма")
    @ExceptionHandler(UserWasNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUserWasNotFoundException(UserWasNotFoundException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }
}
