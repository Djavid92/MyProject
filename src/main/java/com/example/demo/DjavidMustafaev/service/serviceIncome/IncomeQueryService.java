package com.example.demo.DjavidMustafaev.service.serviceIncome;

import com.example.demo.DjavidMustafaev.dto.IncomeDto;
import com.example.demo.DjavidMustafaev.mapper.IncomeExpenseMapper;
import com.example.demo.DjavidMustafaev.repositories.IncomeRepository;
import com.example.demo.DjavidMustafaev.service.MonthlyTotalCalculator;
import com.example.demo.DjavidMustafaev.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
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

    @Lazy
    @Autowired
    private IncomeQueryService incomeQueryService;

    // лист доходов за выбранный период
    @Cacheable(value = "incomes", key = "#startDate + '::' + #endDate")
    public List<IncomeDto> list(LocalDate startDate, LocalDate endDate) {
        return incomeRepository.findIncomesByDateRange(startDate, endDate).stream()
                .map(incomeExpenseMapper::toIncomeDto).toList();

    }

    // сумма доходов за конкретный месяц
    @Cacheable(value = "incomeTotalForYearMonth", key = "#year + '::' + #month")
    @Override
    public BigDecimal totalForYearMonth(int year, int month) {
        return incomeRepository.sumAmountBetween(util.getStartAndEndDate(year, month).get("startDate"),
                util.getStartAndEndDate(year, month).get("endDate")); // достает из мапы в классе Util начальное и конечное значение
    }

    // сумма доходов для текущего месяца
    @Cacheable(value = "incomeTotalForCurrentMonth", key = "T(java.time.LocalDate).now().withDayOfMonth(1)")
    @Override
    public BigDecimal totalForCurrentMonth() {
        return incomeQueryService.totalForYearMonth(util.getCurrentDateInTimeZone().getYear(),
                util.getCurrentDateInTimeZone().getMonthValue()); // достает из метода класса Util месяц и год
    }

}
