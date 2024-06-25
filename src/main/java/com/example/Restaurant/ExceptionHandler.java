package com.example.Restaurant;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalArgumentException.class)
    public void handle(IllegalArgumentException exception) {
        System.out.println("IllegalArgumentException: " + exception.getMessage());
        // bad request
    }

}
