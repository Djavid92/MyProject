package com.example.demo.DjavidMustafaev.service.serviceIncome;

import com.example.demo.DjavidMustafaev.dto.IncomeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceFacadeIncome {
    private final IncomeCommandService incomeCmd;
    private final IncomeQueryService incomeQuery;

    // лист дохода полностью
    public List<IncomeDto> listIncome() {
        return incomeQuery.list();
    }

    // сумма полного дохода
    public BigDecimal totalIncome() {
        return incomeQuery.total();
    }

    // сумма дохода за текущий месяц
    public BigDecimal totalIncomeForCurrentMonth() {
        return incomeQuery.totalIncomeForCurrentMonth();
    }

    // сумма дохода за выбранный месяц
    public BigDecimal totalIncomeFor(int year, int month) {
        return incomeQuery.totalForYearMonth(year, month);
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
