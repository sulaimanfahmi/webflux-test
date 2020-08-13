package com.example.demo.errorHandler;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

}
