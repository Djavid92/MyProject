package com.example.demo.DjavidMustafaev.service;

import com.example.demo.DjavidMustafaev.dto.IncomeDto;
import com.example.demo.DjavidMustafaev.mapper.IncomeExpenseMapper;
import com.example.demo.DjavidMustafaev.repositories.IncomeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class IncomeCommandService {
    private final IncomeRepository incomeRepository;
    private final IncomeExpenseMapper incomeExpenseMapper;

    void save(IncomeDto dto) {
        incomeRepository.save(incomeExpenseMapper.toIncomeEntity(dto));
        log.info("Доход сохранён: {}", dto);
    }
    boolean delete(Long id) {
        if (!incomeRepository.existsById(id)) return false;
        incomeRepository.deleteById(id);
        return true;
    }
    void deleteAll() { incomeRepository.deleteAll(); }
}
