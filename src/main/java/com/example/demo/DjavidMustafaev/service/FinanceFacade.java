package com.example.demo.DjavidMustafaev.service;

import com.example.demo.DjavidMustafaev.dto.ExpenseDto;
import com.example.demo.DjavidMustafaev.dto.IncomeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceFacade {
    private final IncomeQueryService incomeQuery;
    private final ExpenseQueryService expenseQuery;
    private final IncomeCommandService incomeCmd;
    private final ExpenseCommandService expenseCmd;

    public List<IncomeDto> listIncome() {
        return incomeQuery.list();
    }

    public List<ExpenseDto> listExpenses() {
        return expenseQuery.list();
    }

    public BigDecimal totalIncome() {
        return incomeQuery.total();
    }

    public BigDecimal totalExpenses() {
        return expenseQuery.total();
    }

    @Transactional
    public void addIncome(IncomeDto dto) {
        incomeCmd.save(dto);
    }

    @Transactional
    public void addExpense(ExpenseDto dto) {
        expenseCmd.save(dto);
    }

    @Transactional
    public boolean deleteIncome(Long id) {
        return incomeCmd.delete(id);
    }

    @Transactional
    public boolean deleteExpense(Long id) {
        return expenseCmd.delete(id);
    }

    @Transactional
    public void deleteAll() {
        expenseCmd.deleteAll();
        incomeCmd.deleteAll();
    }
}
