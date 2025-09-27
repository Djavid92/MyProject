package com.example.demo.DjavidMustafaev.service;

import com.example.demo.DjavidMustafaev.dto.ExpenseDto;
import com.example.demo.DjavidMustafaev.dto.IncomeDto;
import com.example.demo.DjavidMustafaev.mapper.IncomeExpenseMapper;
import com.example.demo.DjavidMustafaev.model.Expense;
import com.example.demo.DjavidMustafaev.model.Income;
import com.example.demo.DjavidMustafaev.repositories.ExpenseRepository;
import com.example.demo.DjavidMustafaev.repositories.IncomeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FinanceService {

    final private ExpenseRepository expenseRepository;
    final private IncomeRepository incomeRepository;
    final private IncomeExpenseMapper incomeExpenseMapper;

    @Autowired
    public FinanceService(ExpenseRepository expenseRepository, IncomeRepository incomeRepository,
                          IncomeExpenseMapper incomeExpenseMapper) {
        this.expenseRepository = expenseRepository;
        this.incomeRepository = incomeRepository;
        this.incomeExpenseMapper = incomeExpenseMapper;
    }

    @Transactional
    public List<IncomeDto> getAllIncome() {
        return incomeRepository.findAll().stream()
                .map(incomeExpenseMapper::toIncomeDto)
                .collect(Collectors.toList()); // полный список дохода
    }

    @Transactional
    public List<ExpenseDto> getAllExpense() {
        return expenseRepository.findAll().stream().
                map(incomeExpenseMapper::toExpenseDto)
                .collect(Collectors.toList()); // полный список расхода
    }

    @Transactional
    public BigDecimal calculateTotalExpenses() {
        return expenseRepository.findAll().stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add); // общая сумма расхода
    }

    @Transactional
    public BigDecimal calculateTotalIncome() {
        return incomeRepository.findAll().stream()
                .map(Income::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add); // общая сумма дохода
    }

    @Transactional
    public void saveExpense(ExpenseDto expenseDto) {
        final Expense expense = incomeExpenseMapper.toExpenseEntity(expenseDto); // сохранение доходов
        expenseRepository.save(expense);
        log.info("Расход успешно сохранен: {}", expenseDto);
    }

    @Transactional
    public void saveIncome(IncomeDto incomeDto) {
        final Income income = incomeExpenseMapper.toIncomeEntity(incomeDto); // сохранение расходов
        incomeRepository.save(income);
    }

    @Transactional
    public void deleteAll() {
        incomeRepository.deleteAll();
        expenseRepository.deleteAll();
    }

    @Transactional
    public void deleteIncomeOperation(Long id) {
        incomeRepository.deleteById(id);
    }

    @Transactional
    public void deleteExpenseOperation(Long id) {
        expenseRepository.deleteById(id);
    }

//    @Transactional
//    public void testException() {
//        // Имитация исключения
//        throw new DataIntegrityViolationException("Тестовое исключение целостности данных");
//    }
//
//    @Transactional
//    public void testNullPointer() {
//        // Имитация NullPointerException
//        String nullString = null;
//        nullString.length();
//    }
}
