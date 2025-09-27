package com.example.demo.DjavidMustafaev.service;

import com.example.demo.DjavidMustafaev.dto.ExpenseDto;
import com.example.demo.DjavidMustafaev.mapper.IncomeExpenseMapper;
import com.example.demo.DjavidMustafaev.repositories.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenseQueryService {
    private final ExpenseRepository expenseRepository;
    private final IncomeExpenseMapper incomeExpenseMapper;

    List<ExpenseDto> list() {
        return expenseRepository.findAll().stream().map(incomeExpenseMapper::toExpenseDto).toList();
    }
    BigDecimal total() {
        return expenseRepository.sumAmount();
    }
}
