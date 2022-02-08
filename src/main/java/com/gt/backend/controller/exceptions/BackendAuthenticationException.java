package com.gt.backend.controller.exceptions;

public class BackendAuthenticationException extends RuntimeException {
    
    public BackendAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

}
