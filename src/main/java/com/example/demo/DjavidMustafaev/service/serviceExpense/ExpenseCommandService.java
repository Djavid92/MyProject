package com.example.demo.DjavidMustafaev.service.serviceExpense;

import com.example.demo.DjavidMustafaev.dto.ExpenseDto;
import com.example.demo.DjavidMustafaev.model.Category;
import com.example.demo.DjavidMustafaev.model.Expense;
import com.example.demo.DjavidMustafaev.repositories.CategoryRepository;
import com.example.demo.DjavidMustafaev.repositories.ExpenseRepository;
import com.example.demo.DjavidMustafaev.util.Util;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ExpenseCommandService {
    private final ExpenseRepository expenseRepository;
    private final Util util;
    private final CategoryRepository categoryRepository;


    public void save(@NotNull ExpenseDto dto) {
        util.isAfterToday(dto);
        Category category = categoryRepository.findById(dto.getCategoryId())
                        .orElseThrow(() -> new IllegalArgumentException("Категория не найдена"));
        Expense expense = new Expense();
        expense.setName(dto.getName());
        expense.setCategory(category);
        expense.setAmount(dto.getAmount());
        expense.setDate(dto.getDate());
        expenseRepository.save(expense);
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
