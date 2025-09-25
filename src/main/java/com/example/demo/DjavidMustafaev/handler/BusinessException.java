package com.example.demo.DjavidMustafaev.handler;

import java.time.LocalDateTime;

public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

}
