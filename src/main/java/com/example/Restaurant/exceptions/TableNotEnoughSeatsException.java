package com.example.Restaurant.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TableNotEnoughSeatsException extends RuntimeException {

    public TableNotEnoughSeatsException() {
    super("Table haven't enough seats");
    }
}
