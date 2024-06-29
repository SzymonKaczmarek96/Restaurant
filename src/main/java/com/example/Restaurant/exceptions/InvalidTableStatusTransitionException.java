package com.example.Restaurant.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InvalidTableStatusTransitionException extends RuntimeException {
    public InvalidTableStatusTransitionException(String message) {
        super(message);
    }
}
