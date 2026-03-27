package com.example.demo.DjavidMustafaev.service.serviceExpense;

import com.example.demo.DjavidMustafaev.dto.ExpenseDto;
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
class FinanceFacadeExpenseTest {

    @Mock
    private ExpenseCommandService expenseCmd;

    @Mock
    private ExpenseQueryService expenseQuery;

    @InjectMocks
    private FinanceFacadeExpense facade;

    @Test
    void listExpenses_shouldDelegateToQuery() {
        LocalDate start = LocalDate.now().minusDays(5);
        LocalDate end = LocalDate.now();

        List<ExpenseDto> expected = List.of(new ExpenseDto());

        when(expenseQuery.list(start, end)).thenReturn(expected);

        List<ExpenseDto> result = facade.listExpenses(start, end);

        assertEquals(expected, result);
        verify(expenseQuery).list(start, end);
    }

    @Test
    void totalExpensesFor_shouldDelegate() {
        when(expenseQuery.totalForYearMonth(2024, 3))
                .thenReturn(BigDecimal.TEN);

        BigDecimal result = facade.totalExpensesFor(2024, 3);

        assertEquals(BigDecimal.TEN, result);
        verify(expenseQuery).totalForYearMonth(2024, 3);
    }

    @Test
    void totalExpenseForCurrentMonth_shouldDelegate() {
        when(expenseQuery.totalForCurrentMonth())
                .thenReturn(BigDecimal.ONE);

        BigDecimal result = facade.totalExpenseForCurrentMonth();

        assertEquals(BigDecimal.ONE, result);
        verify(expenseQuery).totalForCurrentMonth();
    }

    @Test
    void addExpense_shouldCallCommand() {
        ExpenseDto dto = new ExpenseDto();

        facade.addExpense(dto);

        verify(expenseCmd).save(dto);
    }

    @Test
    void updateExpense_shouldReturnResultFromCommand() {
        ExpenseDto dto = new ExpenseDto();
        when(expenseCmd.update(1L, dto)).thenReturn(true);

        boolean result = facade.updateExpense(1L, dto);

        assertTrue(result);
        verify(expenseCmd).update(1L, dto);
    }

    @Test
    void deleteExpense_shouldReturnResult() {
        when(expenseCmd.delete(1L)).thenReturn(true);

        boolean result = facade.deleteExpense(1L);

        assertTrue(result);
        verify(expenseCmd).delete(1L);
    }

}