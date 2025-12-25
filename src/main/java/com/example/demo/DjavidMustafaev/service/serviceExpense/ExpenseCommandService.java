package com.example.demo.DjavidMustafaev.service.serviceExpense;

import com.example.demo.DjavidMustafaev.dto.ExpenseDto;
import com.example.demo.DjavidMustafaev.mapper.CategoryMapper;
import com.example.demo.DjavidMustafaev.mapper.IncomeExpenseMapper;
import com.example.demo.DjavidMustafaev.model.Category;
import com.example.demo.DjavidMustafaev.model.Expense;
import com.example.demo.DjavidMustafaev.repositories.CategoryRepository;
import com.example.demo.DjavidMustafaev.repositories.ExpenseRepository;
import com.example.demo.DjavidMustafaev.util.Util;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
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
    private final CategoryMapper categoryMapper;


    @Caching(evict = {
            @CacheEvict(value = "expenses", allEntries = true),
            @CacheEvict(value = "expenseTotalForYearMonth", allEntries = true),
            @CacheEvict(value = "expenseTotalForCurrentMonth", allEntries = true)})
    public ExpenseDto save(@NotNull ExpenseDto dto) {
        util.isAfterToday(dto);
        Category category = categoryRepository.findById(dto.getCategoryId())
                        .orElseThrow(() -> new IllegalArgumentException("Категория не найдена"));
        Expense expense = Expense.builder()
                .name(dto.getName())
                .category(category)
                .amount(dto.getAmount())
                .description(dto.getDescription())
                .date(dto.getDate())
                .build();
        expenseRepository.save(expense);


        return ExpenseDto.builder()
                .id(expense.getId())
                .name(expense.getName())
                .categoryDto(categoryMapper.toCategoryDto(expense.getCategory()))
                .amount(expense.getAmount())
                .description(expense.getDescription())
                .date(expense.getDate())
                .build();
    }

    @Caching(evict = {
            @CacheEvict(value = "expenses", allEntries = true),
            @CacheEvict(value = "expenseTotalForYearMonth", allEntries = true),
            @CacheEvict(value = "expenseTotalForCurrentMonth", allEntries = true)})
    public boolean delete(Long id) {
        Optional<Expense> expenseOptional = expenseRepository.findById(id);
        expenseOptional.ifPresent(exp -> expenseRepository.deleteById(id));
        return expenseOptional.isPresent();
    }

    @Caching(evict ={
            @CacheEvict(value = "expenses", allEntries = true),
            @CacheEvict(value = "expenseTotalForYearMonth", allEntries = true),
            @CacheEvict(value = "expenseTotalForCurrentMonth", allEntries = true)

    })
    public void deleteAll() {
        expenseRepository.deleteAll();
    }
}
