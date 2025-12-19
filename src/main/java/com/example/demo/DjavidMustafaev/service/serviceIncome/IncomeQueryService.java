package com.example.demo.DjavidMustafaev.service.serviceIncome;

import com.example.demo.DjavidMustafaev.dto.IncomeDto;
import com.example.demo.DjavidMustafaev.mapper.IncomeExpenseMapper;
import com.example.demo.DjavidMustafaev.repositories.IncomeRepository;
import com.example.demo.DjavidMustafaev.service.MonthlyTotalCalculator;
import com.example.demo.DjavidMustafaev.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IncomeQueryService implements MonthlyTotalCalculator {
    private final IncomeRepository incomeRepository;
    private final IncomeExpenseMapper incomeExpenseMapper;
    private final Util util;

    public List<IncomeDto> list(LocalDate startDate, LocalDate endDate) {
        return incomeRepository.findIncomesByDateRange(startDate, endDate).stream()
                .map(incomeExpenseMapper::toIncomeDto).toList();

    }

    // сумма за конкретный месяц
    @Override
    public BigDecimal totalForYearMonth(int year, int month) {
        return incomeRepository.sumAmountBetween(util.getStartAndEndDate(year, month).get("startDate"),
                util.getStartAndEndDate(year, month).get("endDate"));
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
