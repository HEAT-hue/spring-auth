package com.example.spring_auth.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

//Define class to handle exceptions that occurs throughout the class
@RestControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        System.out.println(Arrays.toString(Arrays.stream(ex.getStackTrace()).toArray()));
        return new ResponseEntity<>(ex.getMessage() + " - Global exception handler", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
