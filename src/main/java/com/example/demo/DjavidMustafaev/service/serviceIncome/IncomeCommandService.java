package com.example.demo.DjavidMustafaev.service.serviceIncome;

import com.example.demo.DjavidMustafaev.dto.IncomeDto;
import com.example.demo.DjavidMustafaev.mapper.IncomeExpenseMapper;
import com.example.demo.DjavidMustafaev.model.Income;
import com.example.demo.DjavidMustafaev.repositories.IncomeRepository;
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
public class IncomeCommandService {
    private final IncomeRepository incomeRepository;
    private final IncomeExpenseMapper incomeExpenseMapper;

    public void save(IncomeDto dto) {
        LocalDate today = LocalDate.now();
        if (dto.getDate().isAfter(today)) {
            throw new IllegalArgumentException("Нельзя записывать транзакцию на будущее");
        }
        incomeRepository.save(incomeExpenseMapper.toIncomeEntity(dto));
        log.info("Доход сохранён: {}", dto);
    }
    public boolean delete(Long id) {
        Optional<Income> incomeOptional = incomeRepository.findById(id);
        incomeOptional.ifPresent(inc -> incomeRepository.deleteById(id));
        return incomeOptional.isPresent();
    }
    public void deleteAll() { incomeRepository.deleteAll(); }
}
