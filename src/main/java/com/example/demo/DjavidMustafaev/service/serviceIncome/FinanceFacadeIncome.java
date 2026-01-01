package com.example.demo.DjavidMustafaev.service.serviceIncome;

import com.example.demo.DjavidMustafaev.dto.IncomeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceFacadeIncome {
    private final IncomeCommandService incomeCmd;
    private final IncomeQueryService incomeQuery;

    // лист дохода полностью
    @Cacheable(value = "incomes", key = "#startDate + '::' + #endDate")
    public List<IncomeDto> listIncome(LocalDate startDate, LocalDate endDate) {
        return incomeQuery.list(startDate, endDate);
    }

    // сумма дохода за произвольный месяц
    public BigDecimal totalIncomesFor(int year, int month) {
        return incomeQuery.totalForYearMonth(year, month);
    }

    // сумма дохода за текущий месяц
    public BigDecimal totalIncomeForCurrentMonth() {
        return incomeQuery.totalForCurrentMonth();
    }


    // добавить доход
    @Transactional
    public void addIncome(IncomeDto dto) {
        incomeCmd.save(dto);
    }

    // удалить доход
    @Transactional
    public boolean deleteIncome(Long id) {
        return incomeCmd.delete(id);
    }

    // удалить всё
    @Transactional
    public void deleteAll() {
        incomeCmd.deleteAll();
    }

}
