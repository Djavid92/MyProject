package com.example.demo.DjavidMustafaev.service.serviceExpense;

import com.example.demo.DjavidMustafaev.dto.ExpenseDto;
import com.example.demo.DjavidMustafaev.mapper.IncomeExpenseMapper;
import com.example.demo.DjavidMustafaev.model.Expense;
import com.example.demo.DjavidMustafaev.repositories.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ExpenseCommandService {
    private final ExpenseRepository expenseRepository;
    private final IncomeExpenseMapper incomeExpenseMapper;


    public void save(ExpenseDto dto) {
        LocalDate today = LocalDate.now();
        if (dto.getDate().isAfter(today)) {
            throw new  IllegalArgumentException("Нельзя записывать транзакцию на будущее");
        }
        expenseRepository.save(incomeExpenseMapper.toExpenseEntity(dto));
        log.info("Расход сохранён: {}", dto);
    }

    public boolean delete(Long id) {
        Optional<Expense> expenseOptional = expenseRepository.findById(id);
        expenseOptional.ifPresent(exp -> expenseRepository.deleteById(id));
        return expenseOptional.isPresent();
    }

    public void deleteAll() {
        expenseRepository.deleteAll();
    }
}
