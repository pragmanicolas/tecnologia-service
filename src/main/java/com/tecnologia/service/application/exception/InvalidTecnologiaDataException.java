package com.tecnologia.service.application.exception;

public class InvalidTecnologiaDataException extends RuntimeException {

    public InvalidTecnologiaDataException(String message) {
        super(message);
    }

    public InvalidTecnologiaDataException(String message, Throwable cause) {
        super(message, cause);
    }
}