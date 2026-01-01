package com.example.demo.DjavidMustafaev.service.serviceIncome;

import com.example.demo.DjavidMustafaev.dto.IncomeDto;
import com.example.demo.DjavidMustafaev.model.Category;
import com.example.demo.DjavidMustafaev.model.Income;
import com.example.demo.DjavidMustafaev.repositories.CategoryRepository;
import com.example.demo.DjavidMustafaev.repositories.IncomeRepository;
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
@Transactional(readOnly = true)
@Slf4j
public class IncomeCommandService {
    private final IncomeRepository incomeRepository;
    private final CategoryRepository categoryRepository;

    @Caching(evict ={
            @CacheEvict(value = "incomes", allEntries = true),
            @CacheEvict(value = "incomeTotalForYearMonth", allEntries = true),
            @CacheEvict(value = "incomeTotalForCurrentMonth", allEntries = true)

    })
    public void save(@NotNull IncomeDto dto) {
        Util.isAfterToday(dto.getDate());
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Категория не найдена"));

        Income income = Income.builder()
                .name(dto.getName())
                .category(category)
                .amount(dto.getAmount())
                .description(dto.getDescription())
                .date(dto.getDate())
                .build();
        incomeRepository.save(income);
    }


    @Caching(evict ={
            @CacheEvict(value = "incomes", allEntries = true),
            @CacheEvict(value = "incomeTotalForYearMonth", allEntries = true),
            @CacheEvict(value = "incomeTotalForCurrentMonth", allEntries = true)

    })
    public boolean delete(Long id) {
        Optional<Income> incomeOptional = incomeRepository.findById(id);
        incomeOptional.ifPresent(inc -> incomeRepository.deleteById(id));
        return incomeOptional.isPresent();
    }

    @Caching(evict ={
            @CacheEvict(value = "incomes", allEntries = true),
            @CacheEvict(value = "incomeTotalForYearMonth", allEntries = true),
            @CacheEvict(value = "incomeTotalForCurrentMonth", allEntries = true)

    })
    public void deleteAll() { incomeRepository.deleteAll(); }
}
