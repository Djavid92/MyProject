package com.example.demo.DjavidMustafaev.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
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

    @Test
    void getStartAndEndDate_mapShouldContainBothKeys() {
        Map<String, LocalDate> result = Util.getStartAndEndDate(2024, 6);
        assertTrue(result.containsKey(Util.START_DATE_KEY));
        assertTrue(result.containsKey(Util.END_DATE_KEY));
    }
}
