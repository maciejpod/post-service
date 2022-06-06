package com.maciej.postservice.exceptions;

public class PostSupplierException extends RuntimeException {
    public PostSupplierException(String message, Throwable cause) {
        super(message, cause);
    }

    public PostSupplierException(String message) {
        super(message);
    }
}
