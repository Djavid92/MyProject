package com.example.demo.DjavidMustafaev.service;

import com.example.demo.DjavidMustafaev.dto.IncomeDto;
import com.example.demo.DjavidMustafaev.mapper.IncomeExpenseMapper;
import com.example.demo.DjavidMustafaev.repositories.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IncomeQueryService {
    private final IncomeRepository incomeRepository;
    private final IncomeExpenseMapper incomeExpenseMapper;

    List<IncomeDto> list() {
        return incomeRepository.findAll().stream().map(incomeExpenseMapper::toIncomeDto).toList();
    }
    BigDecimal total() {
        return incomeRepository.sumAmount();
    }
}
