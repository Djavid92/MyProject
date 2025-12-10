package com.example.demo.DjavidMustafaev.service.serviceExpense;

import com.example.demo.DjavidMustafaev.dto.ExpenseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceFacadeExpense {
    private final ExpenseCommandService expenseCmd;
    private final ExpenseQueryService expenseQuery;

    // лист расхода полностью
    public List<ExpenseDto> listExpenses() {
        return expenseQuery.list();
    }

    // сумма расхода за текущий месяц
    public BigDecimal totalExpenseForCurrentMonth() {
        return expenseQuery.totalForCurrentMonth();
    }

    // сумма расхода за прошлый месяц
    public BigDecimal totalExpenseForPreviousMonth() {
        return expenseQuery.totalForPreviousMonth();
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
