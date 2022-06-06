package com.maciej.postservice.exceptions;

public class PostConversionException extends RuntimeException {
    public PostConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
