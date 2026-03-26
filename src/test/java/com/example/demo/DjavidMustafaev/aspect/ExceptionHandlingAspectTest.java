package com.example.demo.DjavidMustafaev.aspect;

import com.example.demo.DjavidMustafaev.handler.BusinessException;
import com.example.demo.DjavidMustafaev.service.TestServiceForAspect;
import org.hibernate.service.spi.ServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExceptionHandlingAspectTest {

    @Autowired
    private TestServiceForAspect testService;


    @Test
    void shouldWrapIllegalArgumentException() {
        assertThrows(BusinessException.class,
                () -> testService.throwIllegalArgument());
    }


    @Test
    void shouldWrapDataIntegrityViolationException() {
        assertThrows(BusinessException.class,
                () -> testService.throwDataIntegrity());
    }


    @Test
    void shouldWrapDataAccessException() {
        assertThrows(ServiceException.class,
                () -> testService.throwDataAccess());
    }


    @Test
    void shouldWrapMappingException() {
        assertThrows(ServiceException.class,
                () -> testService.throwMapping());
    }


    @Test
    void shouldWrapNullPointerException() {
        assertThrows(ServiceException.class,
                () -> testService.throwNullPointer());
    }


    @Test
    void shouldReturnZero_whenMethodStartsWithCalculateTotal() {
        BigDecimal result = testService.calculateTotalSomething();

        assertEquals(BigDecimal.ZERO, result);
    }


    @Test
    void shouldWrapUnknownException() {
        assertThrows(ServiceException.class,
                () -> testService.throwUnknown());
    }


    @Test
    void shouldReturnResult_whenNoException() {
        String result = testService.successMethod();

        assertEquals("OK", result);
    }
}
