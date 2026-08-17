package com.fragala.techstore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // it means that this classs contains exception-handling logic that applies to my REST controllers.
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class) // "Whenever a ResourceNotFoundException reaches the controller layer, execute this method"
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException exception){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }
    
}
