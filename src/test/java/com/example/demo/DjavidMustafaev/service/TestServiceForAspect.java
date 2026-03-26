package com.example.demo.DjavidMustafaev.service;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TestServiceForAspect {

    public void throwIllegalArgument() {
        throw new IllegalArgumentException("bad data");
    }

    public void throwDataIntegrity() {
        throw new DataIntegrityViolationException("db constraint");
    }

    public void throwDataAccess() {
        throw new DataAccessException("db error") {};
    }

    public void throwMapping() {
        throw new org.hibernate.MappingException("mapping error");
    }

    public void throwNullPointer() {
        throw new NullPointerException();
    }

    public BigDecimal calculateTotalSomething() {
        throw new NullPointerException();
    }

    public void throwUnknown() {
        throw new RuntimeException("unknown error");
    }

    public String successMethod() {
        return "OK";
    }
}