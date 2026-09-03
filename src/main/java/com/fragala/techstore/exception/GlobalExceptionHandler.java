package com.fragala.techstore.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // it means that this classs contains exception-handling logic that applies to my REST controllers.
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class) // "Whenever a ResourceNotFoundException reaches the controller layer, execute this method"
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException exception){
        
        ApiError error =  new ApiError(
            HttpStatus.NOT_FOUND.value(),
            exception.getMessage()
        );
        
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(
        MethodArgumentNotValidException exception) {

            List<FieldError> errors = exception.getBindingResult().getFieldErrors();

            Map<String, String> validationErrors = new HashMap<>();

            for (FieldError error : errors){
                validationErrors.put(
                    error.getField(),
                    error.getDefaultMessage() 
                );
            }

            ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                    "Validation failed", validationErrors
                );

            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
        }

        @ExceptionHandler(CategoryAlreadyExistsException.class)
        public ResponseEntity<ApiError> handleCategoryAlreadyExists(CategoryAlreadyExistsException exception){
        
        ApiError error =  new ApiError(
            HttpStatus.CONFLICT.value(),
            exception.getMessage()
        );
        
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }
    
    
}
