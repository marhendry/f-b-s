package com.example.fbs.fbs.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = NotEnoughSeatsException.class)
    ResponseEntity<String> notEnoughSeatsExceptionHandler(NotEnoughSeatsException e) {
        log.warn("CALLED METHOD - [notEnoughSeatsException]");
        return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler(value = NotFoundException.class)
    ResponseEntity<String> notFoundExceptionHandler(NotFoundException e) {
        log.warn("CALLED METHOD - [notFoundException]");
        return new ResponseEntity<>(e.getMessage(), NOT_FOUND);
    }

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<String> genericExceptionHandler(Exception e) {
        log.error("CALLED METHOD - [genericExceptionHandler]");
        return new ResponseEntity<>(e.getMessage(), INTERNAL_SERVER_ERROR);
    }
}
