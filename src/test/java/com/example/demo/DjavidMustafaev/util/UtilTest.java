package com.example.demo.DjavidMustafaev.util;

import com.example.demo.DjavidMustafaev.handler.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {

    @Test
    void getCurrentDateInTimeZone_shouldReturnNonNullDate() {
        LocalDate date = Util.getCurrentDateInTimeZone();
        assertNotNull(date);
    }

    @Test
    void isAfterToday_shouldNotThrow_forPastDate() {
        assertDoesNotThrow(() -> Util.isAfterToday(LocalDate.now().minusDays(1)));
    }

    @Test
    void isAfterToday_shouldNotThrow_forToday() {
        assertDoesNotThrow(() -> Util.isAfterToday(LocalDate.now()));
    }

    @Test
    void isAfterToday_shouldThrow_forFutureDate() {
        LocalDate future = LocalDate.now().plusDays(1);
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> Util.isAfterToday(future)
        );
        assertEquals(Util.MESSAGE_EXCEPTION, ex.getMessage());
    }

    @Test
    void isAfterToday_shouldThrow_forFarFutureDate() {
        LocalDate farFuture = LocalDate.now().plusYears(1);
        assertThrows(IllegalArgumentException.class, () -> Util.isAfterToday(farFuture));
    }

    @Test
    void getStartAndEndDate_shouldReturnFirstAndLastDayOfMonth_leapFebruary() {
        Map<String, LocalDate> result = Util.getStartAndEndDate(2024, 2);
        assertEquals(LocalDate.of(2024, 2, 1), result.get(Util.START_DATE_KEY));
        assertEquals(LocalDate.of(2024, 2, 29), result.get(Util.END_DATE_KEY));
    }

    @Test
    void getStartAndEndDate_shouldReturnFirstAndLastDayOfMonth_nonLeapFebruary() {
        Map<String, LocalDate> result = Util.getStartAndEndDate(2023, 2);
        assertEquals(LocalDate.of(2023, 2, 1), result.get(Util.START_DATE_KEY));
        assertEquals(LocalDate.of(2023, 2, 28), result.get(Util.END_DATE_KEY));
    }

    @Test
    void getStartAndEndDate_shouldReturnCorrect_forJanuary() {
        Map<String, LocalDate> result = Util.getStartAndEndDate(2024, 1);
        assertEquals(LocalDate.of(2024, 1, 1), result.get(Util.START_DATE_KEY));
        assertEquals(LocalDate.of(2024, 1, 31), result.get(Util.END_DATE_KEY));
    }

    @Test
    void getStartAndEndDate_shouldReturnCorrect_forApril() {
        Map<String, LocalDate> result = Util.getStartAndEndDate(2024, 4);
        assertEquals(LocalDate.of(2024, 4, 1), result.get(Util.START_DATE_KEY));
        assertEquals(LocalDate.of(2024, 4, 30), result.get(Util.END_DATE_KEY));
    }

    @Test
    void getStartAndEndDate_shouldReturnCorrect_forDecember() {
        Map<String, LocalDate> result = Util.getStartAndEndDate(2025, 12);
        assertEquals(LocalDate.of(2025, 12, 1), result.get(Util.START_DATE_KEY));
        assertEquals(LocalDate.of(2025, 12, 31), result.get(Util.END_DATE_KEY));
    }

    // ── calculate ─────────────────────────────────────────────────────────────

    @Test
    void calculate_shouldAdd_twoNumbers() {
        BigDecimal result = Util.calculate(List.of(new BigDecimal("100"), new BigDecimal("50")), '+');
        assertEquals(new BigDecimal("150"), result);
    }

    @Test
    void calculate_shouldAdd_multipleNumbers() {
        BigDecimal result = Util.calculate(List.of(new BigDecimal("10"), new BigDecimal("20"), new BigDecimal("30")), '+');
        assertEquals(new BigDecimal("60"), result);
    }

    @Test
    void calculate_shouldSubtract() {
        BigDecimal result = Util.calculate(List.of(new BigDecimal("100"), new BigDecimal("50")), '-');
        assertEquals(new BigDecimal("50"), result);
    }

    @Test
    void calculate_shouldSubtract_multipleNumbers() {
        BigDecimal result = Util.calculate(List.of(new BigDecimal("100"), new BigDecimal("30"), new BigDecimal("20")), '-');
        assertEquals(new BigDecimal("50"), result);
    }

    @Test
    void calculate_shouldMultiply() {
        BigDecimal result = Util.calculate(List.of(new BigDecimal("100"), new BigDecimal("50")), '*');
        assertEquals(new BigDecimal("5000"), result);
    }

    @Test
    void calculate_shouldMultiply_multipleNumbers() {
        BigDecimal result = Util.calculate(List.of(new BigDecimal("2"), new BigDecimal("3"), new BigDecimal("4")), '*');
        assertEquals(new BigDecimal("24"), result);
    }

    @Test
    void calculate_shouldDivideWithScale2() {
        BigDecimal result = Util.calculate(List.of(new BigDecimal("100"), new BigDecimal("3")), '/');
        assertEquals(new BigDecimal("33.33"), result);
    }

    @Test
    void calculate_shouldThrowArithmetic_onDivisionByZero() {
        ArithmeticException ex = assertThrows(ArithmeticException.class,
                () -> Util.calculate(List.of(new BigDecimal("100"), BigDecimal.ZERO), '/'));
        assertEquals("Деление на ноль", ex.getMessage());
    }

    @Test
    void calculate_shouldThrowBusinessException_onUnknownOperator() {
        assertThrows(BusinessException.class,
                () -> Util.calculate(List.of(new BigDecimal("100"), new BigDecimal("50")), '%'));
    }

    @Test
    void calculate_shouldThrowBusinessException_onLessThanTwoNumbers() {
        assertThrows(BusinessException.class,
                () -> Util.calculate(List.of(new BigDecimal("100")), '+'));
    }

    @Test
    void getStartAndEndDate_mapShouldContainBothKeys() {
        Map<String, LocalDate> result = Util.getStartAndEndDate(2024, 6);
        assertTrue(result.containsKey(Util.START_DATE_KEY));
        assertTrue(result.containsKey(Util.END_DATE_KEY));
    }
}
