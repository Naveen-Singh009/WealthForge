package com.example.demo.exceptions;


public class InsufficientStockQuantityException extends RuntimeException {

    public InsufficientStockQuantityException(String message) {
        super(message);
    }
}