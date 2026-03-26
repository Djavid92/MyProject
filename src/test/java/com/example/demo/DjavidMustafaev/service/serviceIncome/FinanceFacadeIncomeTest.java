package com.example.demo.DjavidMustafaev.service.serviceIncome;

import com.example.demo.DjavidMustafaev.dto.IncomeDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinanceFacadeIncomeTest {

    @Mock
    private IncomeCommandService incomeCmd;

    @Mock
    private IncomeQueryService incomeQuery;

    @InjectMocks
    private FinanceFacadeIncome facade;

    @Test
    void listIncome_shouldDelegateToQueryService() {
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now();

        List<IncomeDto> expected = List.of(new IncomeDto());
        when(incomeQuery.list(start, end)).thenReturn(expected);

        List<IncomeDto> result = facade.listIncome(start, end);

        assertEquals(expected, result);
        verify(incomeQuery).list(start, end);
    }

    @Test
    void totalIncomesFor_shouldDelegate() {
        when(incomeQuery.totalForYearMonth(2024, 3))
                .thenReturn(BigDecimal.TEN);

        BigDecimal result = facade.totalIncomesFor(2024, 3);

        assertEquals(BigDecimal.TEN, result);
        verify(incomeQuery).totalForYearMonth(2024, 3);
    }

    @Test
    void addIncome_shouldCallCommandService() {
        IncomeDto dto = new IncomeDto();

        facade.addIncome(dto);

        verify(incomeCmd).save(dto);
    }

    @Test
    void deleteIncome_shouldReturnResultFromCommand() {
        when(incomeCmd.delete(1L)).thenReturn(true);

        boolean result = facade.deleteIncome(1L);

        assertTrue(result);
        verify(incomeCmd).delete(1L);
    }

    @Test
    void deleteAll_shouldCallCommand() {
        facade.deleteAll();

        verify(incomeCmd).deleteAll();
    }
}