package com.example.Restaurant.exceptions;

public class InvalidTableStatusTransitionException extends RuntimeException{
    public InvalidTableStatusTransitionException(String message) {
        super(message);
    }
}
