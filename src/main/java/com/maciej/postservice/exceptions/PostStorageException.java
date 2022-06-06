package com.maciej.postservice.exceptions;

public class PostStorageException extends RuntimeException {

    public PostStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
