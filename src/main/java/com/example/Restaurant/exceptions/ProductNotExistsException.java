package com.example.Restaurant.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotExistsException extends RuntimeException {

    public ProductNotExistsException(String productName) {
        super("Product with name " + productName + " not exists");
    }


}
