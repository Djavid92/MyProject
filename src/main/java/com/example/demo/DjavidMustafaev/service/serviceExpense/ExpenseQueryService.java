package com.example.demo.DjavidMustafaev.service.serviceExpense;

import com.example.demo.DjavidMustafaev.dto.ExpenseDto;
import com.example.demo.DjavidMustafaev.mapper.IncomeExpenseMapper;
import com.example.demo.DjavidMustafaev.repositories.ExpenseRepository;
import com.example.demo.DjavidMustafaev.service.MonthlyTotalCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
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

    @Lazy
    @Autowired
    private ExpenseQueryService expenseQueryService; // self injection

    // лист расходов за выбранный период
    @Cacheable(value = "expenses", key = "#startDate + '::' + #endDate")
    public List<ExpenseDto> list(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findExpenseByDateRange(startDate, endDate).stream()
                .map(incomeExpenseMapper::toExpenseDto).toList();
    }

    // сумма расходов за конкретный месяц
    @Cacheable(value = "expenseTotalForYearMonth", key = "#year + '::' + #month")
    @Override
    public BigDecimal totalForYearMonth(int year, int month) {
        return expenseRepository.sumAmountBetween(util.getStartAndEndDate(year, month).get("startDate"),
                util.getStartAndEndDate(year, month).get("endDate")); // достает из мапы в классе Util начальное и конечное значение
    }

    // сумма расходов для текущего месяца
    @Cacheable(value = "expenseTotalForCurrentMonth", key = "T(java.time.LocalDate).now().withDayOfMonth(1)")
    @Override
    public BigDecimal totalForCurrentMonth() {
        return expenseQueryService.totalForYearMonth(util.getCurrentDateInTimeZone().getYear(),
                util.getCurrentDateInTimeZone().getMonthValue()); // достает из метода класса Util месяц и год
    }

}
