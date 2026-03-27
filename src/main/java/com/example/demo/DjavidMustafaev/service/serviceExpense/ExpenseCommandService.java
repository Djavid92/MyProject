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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseCommandService {
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;


    @Transactional
    @Caching(evict = {
            @CacheEvict(value = ExpenseQueryService.CACHEABLE_EXPENSES_VALUE, allEntries = true),
            @CacheEvict(value = ExpenseQueryService.CACHEABLE_EXPENSES_TOTAL_FOR_YEAR_MONTH_VALUE,
                    key = "#dto.date.year + '::' + #dto.date.monthValue"),
            @CacheEvict(value = ExpenseQueryService.CACHEABLE_EXPENSES_TOTAL_FOR_CURRENT_MONTH_VALUE, allEntries = true)})
    public void save(@NotNull ExpenseDto dto) {
        Util.isAfterToday(dto.getDate());
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
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = ExpenseQueryService.CACHEABLE_EXPENSES_VALUE, allEntries = true),
            @CacheEvict(value = ExpenseQueryService.CACHEABLE_EXPENSES_TOTAL_FOR_YEAR_MONTH_VALUE,
                    key = "#dto.date.year + '::' + #dto.date.monthValue"),
            @CacheEvict(value = ExpenseQueryService.CACHEABLE_EXPENSES_TOTAL_FOR_CURRENT_MONTH_VALUE, allEntries = true)})
    public boolean update(Long id, @NotNull ExpenseDto dto) {
        return expenseRepository.findById(id).map(expense -> {
            Util.isAfterToday(dto.getDate());
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Категория не найдена"));
            expense.setName(dto.getName());
            expense.setCategory(category);
            expense.setAmount(dto.getAmount());
            expense.setDescription(dto.getDescription());
            expense.setDate(dto.getDate());
            expenseRepository.save(expense);
            return true;
        }).orElse(false);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = ExpenseQueryService.CACHEABLE_EXPENSES_VALUE, allEntries = true),
            @CacheEvict(value = ExpenseQueryService.CACHEABLE_EXPENSES_TOTAL_FOR_YEAR_MONTH_VALUE, allEntries = true),
            @CacheEvict(value = ExpenseQueryService.CACHEABLE_EXPENSES_TOTAL_FOR_CURRENT_MONTH_VALUE, allEntries = true)})
    public boolean delete(Long id) {
        Optional<Expense> expenseOptional = expenseRepository.findById(id);
        expenseOptional.ifPresent(exp -> expenseRepository.deleteById(id));
        return expenseOptional.isPresent();
    }

}
