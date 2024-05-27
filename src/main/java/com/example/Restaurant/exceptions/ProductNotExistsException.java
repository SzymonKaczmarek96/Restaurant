package com.example.Restaurant.exceptions;

public class ProductNotExistsException extends RuntimeException {

    public ProductNotExistsException(String productName){
        super("Product with name " + productName + " not exists");
    }

}
