package com.example.Restaurant.exceptions;

//TODO: ResponseStatus(HttpStatus.CONFLICT)
public class InvalidTableStatusTransitionException extends RuntimeException {
    public InvalidTableStatusTransitionException(String message) {
        super(message);
    }
}
