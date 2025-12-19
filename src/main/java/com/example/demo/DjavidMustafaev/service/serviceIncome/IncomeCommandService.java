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
    private final Util util;

    public void save(@NotNull IncomeDto dto) {
        util.isAfterToday(dto);
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Категория не найдена"));
        Income income = new Income();
        income.setName(dto.getName());
        income.setCategory(category);
        income.setAmount(dto.getAmount());
        income.setDate(dto.getDate());
        incomeRepository.save(income);
    }
    public boolean delete(Long id) {
        Optional<Income> incomeOptional = incomeRepository.findById(id);
        incomeOptional.ifPresent(inc -> incomeRepository.deleteById(id));
        return incomeOptional.isPresent();
    }
    public void deleteAll() { incomeRepository.deleteAll(); }
}
