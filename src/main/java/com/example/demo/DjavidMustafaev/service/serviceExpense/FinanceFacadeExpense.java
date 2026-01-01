package com.example.demo.DjavidMustafaev.service.serviceExpense;

import com.example.demo.DjavidMustafaev.dto.ExpenseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceFacadeExpense {
    private final ExpenseCommandService expenseCmd;
    private final ExpenseQueryService expenseQuery;

    // лист расхода полностью
    public List<ExpenseDto> listExpenses(LocalDate startDate, LocalDate endDate) {
        return expenseQuery.list(startDate, endDate);
    }

    // сумма расхода за произвольный месяц
    public BigDecimal totalExpensesFor(int year, int month) {
        return expenseQuery.totalForYearMonth(year, month);
    }

    // сумма расхода за текущий месяц
    public BigDecimal totalExpenseForCurrentMonth() {
        return expenseQuery.totalForCurrentMonth();
    }


    // добавить расход
    @Transactional
    public void addExpense(ExpenseDto dto) {
        expenseCmd.save(dto);
    }

    // удалить расход
    @Transactional
    public boolean deleteExpense(Long id) {
        return expenseCmd.delete(id);
    }

    // удалить всё
    @Transactional
    public void deleteAll() {
        expenseCmd.deleteAll();
    }
}
