package com.example.demo.DjavidMustafaev.service.serviceIncome;

import com.example.demo.DjavidMustafaev.dto.IncomeDto;
import com.example.demo.DjavidMustafaev.mapper.IncomeExpenseMapper;
import com.example.demo.DjavidMustafaev.repositories.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IncomeQueryService {
    private final IncomeRepository incomeRepository;
    private final IncomeExpenseMapper incomeExpenseMapper;

    public List<IncomeDto> list() {
        return incomeRepository.findAll().stream().map(incomeExpenseMapper::toIncomeDto).toList();
    }
    public BigDecimal total() {
        return incomeRepository.sumAmount();
    }

    // Новая — сумма за конкретный месяц
    public BigDecimal totalForYearMonth(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return incomeRepository.sumAmountBetween(start, end);
    }

    // Удобный метод — сумма для текущего месяца
    public BigDecimal totalIncomeForCurrentMonth() {
        ZoneId zone = ZoneId.of("Asia/Yekaterinburg");
        LocalDate now = LocalDate.now(zone);
        return totalForYearMonth(now.getYear(), now.getMonthValue());
    }
}
