package com.fragala.techstore.exception;

public class BrandAlreadyExistsException extends RuntimeException {
    
    public BrandAlreadyExistsException (String message){
        super(message);
    }
}
