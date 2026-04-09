package com.example.demo.DjavidMustafaev.service.serviceIncome;

import com.example.demo.DjavidMustafaev.dto.CategoryDto;
import com.example.demo.DjavidMustafaev.dto.IncomeDto;
import com.example.demo.DjavidMustafaev.mapper.IncomeExpenseMapper;
import com.example.demo.DjavidMustafaev.model.Category;
import com.example.demo.DjavidMustafaev.repositories.CategoryRepository;
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
public class IncomeQueryService implements MonthlyTotalCalculator <IncomeDto> {
    public static final String CACHEABLE_INCOMES_VALUE = "incomes";
    public static final String CACHEABLE_INCOMES_CATEGORY_VALUE = "incomesCategoryValue";
    public static final String CACHEABLE_INCOMES_TOTAL_FOR_YEAR_MONTH_VALUE = "incomeTotalForYearMonth";
    public static final String CACHEABLE_INCOMES_TOTAL_FOR_CURRENT_MONTH_VALUE = "incomeTotalForCurrentMonth";

    private final IncomeRepository incomeRepository;
    private final IncomeExpenseMapper incomeExpenseMapper;
    private final CategoryRepository categoryRepository;

    @Lazy
    @Autowired
    private IncomeQueryService incomeQueryService;

    // лист доходов за выбранный период
    @Cacheable(value = CACHEABLE_INCOMES_VALUE, key = "#startDate + '::' + #endDate")
    @Override
    public List<IncomeDto> list(LocalDate startDate, LocalDate endDate) {
        return incomeRepository.findIncomesByDateRange(startDate, endDate).stream()
                .map(incomeExpenseMapper::toIncomeDto).toList();

    }

    // лист доходов по категориям
    @Cacheable(value = CACHEABLE_INCOMES_CATEGORY_VALUE, key = "#categoryDto.name")
    @Override
    public List<IncomeDto> list(CategoryDto categoryDto) {
        Category category = categoryRepository.findByName(categoryDto.getName())
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        return incomeRepository.findIncomeByCategory(category).stream()
                .map(incomeExpenseMapper::toIncomeDto).toList();
    }

    // сумма доходов за конкретный месяц
    @Cacheable(value = CACHEABLE_INCOMES_TOTAL_FOR_YEAR_MONTH_VALUE, key = "#year + '::' + #month")
    @Override
    public BigDecimal totalForYearMonth(int year, int month) {
        return incomeRepository.sumAmountBetween(Util.getStartAndEndDate(year, month).get(Util.START_DATE_KEY),
                Util.getStartAndEndDate(year, month).get(Util.END_DATE_KEY)); // достает из мапы в классе Util начальное и конечное значение
    }

    // сумма доходов для текущего месяца
    @Cacheable(value = CACHEABLE_INCOMES_TOTAL_FOR_CURRENT_MONTH_VALUE, key = "T(java.time.LocalDate).now().withDayOfMonth(1)")
    @Override
    public BigDecimal totalForCurrentMonth() {
        return incomeQueryService.totalForYearMonth(Util.getCurrentDateInTimeZone().getYear(),
                Util.getCurrentDateInTimeZone().getMonthValue()); // достает из метода класса Util месяц и год
    }

}
