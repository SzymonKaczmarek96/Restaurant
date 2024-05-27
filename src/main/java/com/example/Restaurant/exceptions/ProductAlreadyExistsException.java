package com.example.Restaurant.exceptions;

public class ProductAlreadyExistsException extends RuntimeException{

    public ProductAlreadyExistsException(String productName) {
        super("Product with name " + productName + " already exists.");
    }
}
