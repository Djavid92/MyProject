package djavidmustafaev.io.financetracker.service.serviceExpense;

import djavidmustafaev.io.financetracker.dto.CategoryDto;
import djavidmustafaev.io.financetracker.dto.ExpenseDto;
import djavidmustafaev.io.financetracker.mapper.IncomeExpenseMapper;
import djavidmustafaev.io.financetracker.model.Category;
import djavidmustafaev.io.financetracker.repositories.CategoryRepository;
import djavidmustafaev.io.financetracker.repositories.ExpenseRepository;
import djavidmustafaev.io.financetracker.service.MonthlyTotalCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import djavidmustafaev.io.financetracker.util.Util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenseQueryService implements MonthlyTotalCalculator <ExpenseDto> {
    public static final String CACHEABLE_EXPENSES_VALUE = "expenses";
    public static final String CACHEABLE_EXPENSES_CATEGORY_VALUE = "expenseCategoryValue";
    public static final String CACHEABLE_EXPENSES_TOTAL_FOR_YEAR_MONTH_VALUE = "expenseTotalForYearMonth";
    public static final String CACHEABLE_EXPENSES_TOTAL_FOR_CURRENT_MONTH_VALUE = "expenseTotalForCurrentMonth";

    private final ExpenseRepository expenseRepository;
    private final IncomeExpenseMapper incomeExpenseMapper;
    private final CategoryRepository categoryRepository;

    @Lazy
    @Autowired
    private ExpenseQueryService expenseQueryService;

    // лист расходов за выбранный период
    @Cacheable(value = CACHEABLE_EXPENSES_VALUE, key = "#startDate + '::' + #endDate")
    @Override
    public List<ExpenseDto> list(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findExpenseByDateRange(startDate, endDate).stream()
                .map(incomeExpenseMapper::toExpenseDto).toList();
    }

    // лист доходов по категориям
    @Cacheable(value = CACHEABLE_EXPENSES_CATEGORY_VALUE, key = "#categoryDto.name")
    @Override
    public List<ExpenseDto> list(CategoryDto categoryDto) {
        Category category = categoryRepository.findByName(categoryDto.getName())
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        return expenseRepository.findIncomeByCategory(category).stream()
                .map(incomeExpenseMapper::toExpenseDto).toList();
    }

    // сумма расходов за конкретный месяц
    @Cacheable(value = CACHEABLE_EXPENSES_TOTAL_FOR_YEAR_MONTH_VALUE, key = "#year + '::' + #month")
    @Override
    public BigDecimal totalForYearMonth(int year, int month) {
        return expenseRepository.sumAmountBetween(Util.getStartAndEndDate(year, month).get("startDate"),
                Util.getStartAndEndDate(year, month).get("endDate")); // достает из мапы в классе Util начальное и конечное значение
    }

    // сумма расходов для текущего месяц
    @Cacheable(value = CACHEABLE_EXPENSES_TOTAL_FOR_CURRENT_MONTH_VALUE, key = "T(java.time.LocalDate).now().withDayOfMonth(1)")
    @Override
    public BigDecimal totalForCurrentMonth() {
        return expenseQueryService.totalForYearMonth(Util.getCurrentDateInTimeZone().getYear(),
                Util.getCurrentDateInTimeZone().getMonthValue()); // достает из метода класса Util месяц и год
    }

}
