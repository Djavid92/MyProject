package com.example.demo.DjavidMustafaev.handler;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.MappingException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, String>> handleBusiness(BusinessException e) {
        log.warn("BusinessException: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(ArithmeticException.class)
    public ResponseEntity<Map<String, String>> handleArithmetic(ArithmeticException e) {
        log.error("ArithmeticException: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        log.error("IllegalArgumentException: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(Map.of("message", "Неверный аргумент: " + e.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrity(DataIntegrityViolationException e) {
        log.error("DataIntegrityViolationException: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(Map.of("message", "Ошибка целостности данных"));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, String>> handleDataAccess(DataAccessException e) {
        log.error("DataAccessException: {}", e.getMessage());
        return ResponseEntity.internalServerError()
                .body(Map.of("message", "Ошибка при работе с базой данных"));
    }

    @ExceptionHandler(MappingException.class)
    public ResponseEntity<Map<String, String>> handleMapping(MappingException e) {
        log.error("MappingException: {}", e.getMessage());
        return ResponseEntity.internalServerError()
                .body(Map.of("message", "Ошибка преобразования данных"));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, String>> handleNpe(NullPointerException e) {
        log.error("NullPointerException: {}", e.getMessage(), e);
        return ResponseEntity.internalServerError()
                .body(Map.of("message", "Внутренняя ошибка: обнаружены null значения"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception e) {
        log.error("Неожиданная ошибка: {}", e.getMessage(), e);
        return ResponseEntity.internalServerError()
                .body(Map.of("message", "Внутренняя ошибка сервиса"));
    }
}
