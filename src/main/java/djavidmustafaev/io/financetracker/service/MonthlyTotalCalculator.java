package djavidmustafaev.io.financetracker.service;

import djavidmustafaev.io.financetracker.dto.CategoryDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface MonthlyTotalCalculator <T> {
    List<T> list(LocalDate startDate, LocalDate endDate);
    List<T> list(CategoryDto categoryDto);
    BigDecimal totalForCurrentMonth();
    BigDecimal totalForYearMonth(int year, int month);
}
