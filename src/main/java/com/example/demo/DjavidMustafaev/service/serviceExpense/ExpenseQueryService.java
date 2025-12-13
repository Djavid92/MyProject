package com.example.demo.DjavidMustafaev.service.serviceExpense;

import com.example.demo.DjavidMustafaev.dto.ExpenseDto;
import com.example.demo.DjavidMustafaev.mapper.IncomeExpenseMapper;
import com.example.demo.DjavidMustafaev.repositories.ExpenseRepository;
import com.example.demo.DjavidMustafaev.service.MonthlyTotalCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.DjavidMustafaev.util.Util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenseQueryService implements MonthlyTotalCalculator {
    private final ExpenseRepository expenseRepository;
    private final IncomeExpenseMapper incomeExpenseMapper;
    private final Util util;


    public List<ExpenseDto> list(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findExpenseByDateRange(startDate, endDate).stream()
                .map(incomeExpenseMapper::toExpenseDto).toList();
    }

    // сумма за конкретный месяц
    @Override
    public BigDecimal totalForYearMonth(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return expenseRepository.sumAmountBetween(start, end);
    }

    // сумма для текущего месяца
    @Override
    public BigDecimal totalForCurrentMonth() {
        return totalForYearMonth(util.getCurrentDateInTimeZone().getYear(), util.getCurrentDateInTimeZone().getMonthValue());
    }

    // сумма за прошлый месяц
    @Override
    public BigDecimal totalForPreviousMonth() {
        LocalDate firstDayOfPreviousMonth = util.getCurrentDateInTimeZone().minusMonths(1).withDayOfMonth(1);
        LocalDate lastDayOfPreviousMonth = util.getCurrentDateInTimeZone().minusMonths(1)
                .withDayOfMonth(util.getCurrentDateInTimeZone().minusMonths(1).lengthOfMonth());
        return totalForYearMonth(firstDayOfPreviousMonth.getYear(), lastDayOfPreviousMonth.getMonthValue());
    }
}
