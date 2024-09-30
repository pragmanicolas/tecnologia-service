package com.tecnologia.service.application.exception;

public class TecnologiaAlreadyExistsException extends RuntimeException {

    public TecnologiaAlreadyExistsException(String message) {
        super(message);
    }

    public TecnologiaAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
