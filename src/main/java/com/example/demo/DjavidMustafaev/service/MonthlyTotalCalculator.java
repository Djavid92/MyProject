package com.example.demo.DjavidMustafaev.service;

import java.math.BigDecimal;

public interface MonthlyTotalCalculator {
    BigDecimal totalForCurrentMonth();
    BigDecimal totalForYearMonth(int year, int month);
}
