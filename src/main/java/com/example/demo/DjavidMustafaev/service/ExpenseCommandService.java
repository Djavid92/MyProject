package com.example.demo.DjavidMustafaev.service;

import com.example.demo.DjavidMustafaev.dto.ExpenseDto;
import com.example.demo.DjavidMustafaev.mapper.IncomeExpenseMapper;
import com.example.demo.DjavidMustafaev.repositories.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ExpenseCommandService {
    private final ExpenseRepository expenseRepository;
    private final IncomeExpenseMapper incomeExpenseMapper;


    void save(ExpenseDto dto) {
        expenseRepository.save(incomeExpenseMapper.toExpenseEntity(dto));
        log.info("Расход сохранён: {}", dto);
    }
    boolean delete(Long id) {
        if (!expenseRepository.existsById(id)) return false;
        expenseRepository.deleteById(id);
        return true;
    }
    void deleteAll() { expenseRepository.deleteAll(); }
}
