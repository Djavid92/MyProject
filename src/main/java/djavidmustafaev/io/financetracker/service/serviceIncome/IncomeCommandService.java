package djavidmustafaev.io.financetracker.service.serviceIncome;

import djavidmustafaev.io.financetracker.dto.IncomeDto;
import djavidmustafaev.io.financetracker.model.Category;
import djavidmustafaev.io.financetracker.model.Income;
import djavidmustafaev.io.financetracker.repositories.CategoryRepository;
import djavidmustafaev.io.financetracker.repositories.IncomeRepository;
import djavidmustafaev.io.financetracker.util.Util;
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
public class IncomeCommandService {
    private final IncomeRepository incomeRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = IncomeQueryService.CACHEABLE_INCOMES_VALUE, allEntries = true),
            @CacheEvict(value = IncomeQueryService.CACHEABLE_INCOMES_TOTAL_FOR_YEAR_MONTH_VALUE,
                    key = "#dto.date.year + '::' + #dto.date.monthValue"),
            @CacheEvict(value = IncomeQueryService.CACHEABLE_INCOMES_TOTAL_FOR_CURRENT_MONTH_VALUE, allEntries = true)
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


    @Transactional
    @Caching(evict = {
            @CacheEvict(value = IncomeQueryService.CACHEABLE_INCOMES_VALUE, allEntries = true),
            @CacheEvict(value = IncomeQueryService.CACHEABLE_INCOMES_TOTAL_FOR_YEAR_MONTH_VALUE,
                    key = "#dto.date.year + '::' + #dto.date.monthValue"),
            @CacheEvict(value = IncomeQueryService.CACHEABLE_INCOMES_TOTAL_FOR_CURRENT_MONTH_VALUE, allEntries = true)
    })
    public boolean update(Long id, @NotNull IncomeDto dto) {
        return incomeRepository.findById(id).map(income -> {
            Util.isAfterToday(dto.getDate());
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Категория не найдена"));
            income.setName(dto.getName());
            income.setCategory(category);
            income.setAmount(dto.getAmount());
            income.setDescription(dto.getDescription());
            income.setDate(dto.getDate());
            incomeRepository.save(income);
            return true;
        }).orElse(false);
    }

    @Transactional
    @Caching(evict ={
            @CacheEvict(value = IncomeQueryService.CACHEABLE_INCOMES_VALUE, allEntries = true),
            @CacheEvict(value = IncomeQueryService.CACHEABLE_INCOMES_TOTAL_FOR_YEAR_MONTH_VALUE, allEntries = true),
            @CacheEvict(value = IncomeQueryService.CACHEABLE_INCOMES_TOTAL_FOR_CURRENT_MONTH_VALUE, allEntries = true)

    })
    public boolean delete(Long id) {
        Optional<Income> incomeOptional = incomeRepository.findById(id);
        incomeOptional.ifPresent(inc -> incomeRepository.deleteById(id));
        return incomeOptional.isPresent();
    }

}
