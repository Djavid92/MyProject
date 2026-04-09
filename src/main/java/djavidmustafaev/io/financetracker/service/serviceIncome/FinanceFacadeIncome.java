package djavidmustafaev.io.financetracker.service.serviceIncome;

import djavidmustafaev.io.financetracker.dto.CategoryDto;
import djavidmustafaev.io.financetracker.dto.IncomeDto;
import lombok.RequiredArgsConstructor;
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

    // лист дохода по месяцам
    public List<IncomeDto> listIncome(LocalDate startDate, LocalDate endDate) {
        return incomeQuery.list(startDate, endDate);
    }

    // лист доходов по категориям
    public List<IncomeDto> listIncome(CategoryDto categoryDto) {
        return incomeQuery.list(categoryDto);
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

    // обновить доход
    @Transactional
    public boolean updateIncome(Long id, IncomeDto dto) {
        return incomeCmd.update(id, dto);
    }

    // удалить доход
    @Transactional
    public boolean deleteIncome(Long id) {
        return incomeCmd.delete(id);
    }


}
