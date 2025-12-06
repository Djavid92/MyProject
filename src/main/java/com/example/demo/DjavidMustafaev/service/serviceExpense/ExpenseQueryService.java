package com.example.demo.DjavidMustafaev.service.serviceExpense;

import com.example.demo.DjavidMustafaev.dto.ExpenseDto;
import com.example.demo.DjavidMustafaev.mapper.IncomeExpenseMapper;
import com.example.demo.DjavidMustafaev.repositories.ExpenseRepository;
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
public class ExpenseQueryService {
    private final ExpenseRepository expenseRepository;
    private final IncomeExpenseMapper incomeExpenseMapper;

    public List<ExpenseDto> list() {
        return expenseRepository.findAll().stream().map(incomeExpenseMapper::toExpenseDto).toList();
    }

    public BigDecimal total() {
        return expenseRepository.sumAmount();
    }

    // Новая — сумма за конкретный месяц
    public BigDecimal totalForYearMonth(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return expenseRepository.sumAmountBetween(start, end);
    }

    // Удобный метод — сумма для текущего месяца (используй timezone Europe/Amsterdam, если важно)
    public BigDecimal totalExpenseForCurrentMonth() {
        ZoneId zone = ZoneId.of("Asia/Yekaterinburg");
        LocalDate now = LocalDate.now(zone);
        return totalForYearMonth(now.getYear(), now.getMonthValue());
    }
}
