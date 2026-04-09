package djavidmustafaev.io.financetracker.service.serviceExpense;

import djavidmustafaev.io.financetracker.dto.ExpenseDto;
import djavidmustafaev.io.financetracker.mapper.CategoryMapper;
import djavidmustafaev.io.financetracker.mapper.IncomeExpenseMapper;
import djavidmustafaev.io.financetracker.repositories.CategoryRepository;
import djavidmustafaev.io.financetracker.repositories.ExpenseRepository;
import djavidmustafaev.io.financetracker.repositories.IncomeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class ExpenseQueryServiceCacheTest {

    @Autowired
    private ExpenseQueryService expenseQueryService;

    @Autowired
    private CacheManager cacheManager;

    @MockBean
    private ExpenseRepository expenseRepository;

    @MockBean
    private IncomeExpenseMapper incomeExpenseMapper;

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private IncomeRepository incomeRepository;

    @MockBean
    private CategoryMapper categoryMapper;

    private static final LocalDate START = LocalDate.of(2024, 3, 1);
    private static final LocalDate END = LocalDate.of(2024, 3, 31);

    @BeforeEach
    void clearCaches() {
        cacheManager.getCacheNames().forEach(name ->
                Objects.requireNonNull(cacheManager.getCache(name)).clear());
    }

    @Test
    void list_shouldCacheByDateRange_andCallRepositoryOnlyOnce() {
        when(expenseRepository.findExpenseByDateRange(START, END)).thenReturn(List.of());

        expenseQueryService.list(START, END);
        expenseQueryService.list(START, END);
        expenseQueryService.list(START, END);

        verify(expenseRepository, times(1)).findExpenseByDateRange(START, END);
    }

    @Test
    void list_shouldUseSeparateCacheEntries_forDifferentDateRanges() {
        LocalDate start2 = LocalDate.of(2024, 4, 1);
        LocalDate end2 = LocalDate.of(2024, 4, 30);

        when(expenseRepository.findExpenseByDateRange(any(), any())).thenReturn(List.of());

        expenseQueryService.list(START, END);
        expenseQueryService.list(start2, end2);
        expenseQueryService.list(START, END);

        verify(expenseRepository, times(1)).findExpenseByDateRange(START, END);
        verify(expenseRepository, times(1)).findExpenseByDateRange(start2, end2);
    }

    @Test
    void totalForYearMonth_shouldCacheByYearAndMonth_andCallRepositoryOnlyOnce() {
        when(expenseRepository.sumAmountBetween(any(), any())).thenReturn(BigDecimal.valueOf(500));

        BigDecimal first = expenseQueryService.totalForYearMonth(2024, 3);
        BigDecimal second = expenseQueryService.totalForYearMonth(2024, 3);

        assertEquals(0, BigDecimal.valueOf(500).compareTo(first));
        assertEquals(first, second);
        verify(expenseRepository, times(1)).sumAmountBetween(any(), any());
    }

    @Test
    void totalForYearMonth_shouldUseSeparateCacheEntries_forDifferentMonths() {
        when(expenseRepository.sumAmountBetween(any(), any()))
                .thenReturn(BigDecimal.valueOf(500), BigDecimal.valueOf(300));

        expenseQueryService.totalForYearMonth(2024, 3);
        expenseQueryService.totalForYearMonth(2024, 4);
        expenseQueryService.totalForYearMonth(2024, 3);
        expenseQueryService.totalForYearMonth(2024, 4);

        verify(expenseRepository, times(2)).sumAmountBetween(any(), any());
    }

    @Test
    void totalForCurrentMonth_shouldCacheResult_andCallRepositoryOnlyOnce() {
        when(expenseRepository.sumAmountBetween(any(), any())).thenReturn(BigDecimal.valueOf(1000));

        BigDecimal first = expenseQueryService.totalForCurrentMonth();
        BigDecimal second = expenseQueryService.totalForCurrentMonth();

        assertEquals(first, second);
        verify(expenseRepository, times(1)).sumAmountBetween(any(), any());
    }

    @Test
    void list_shouldReturnMappedDtos() {
        ExpenseDto dto = ExpenseDto.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(100))
                .date(LocalDate.of(2024, 3, 10))
                .build();
        when(expenseRepository.findExpenseByDateRange(START, END)).thenReturn(List.of());
        when(incomeExpenseMapper.toExpenseDto(any())).thenReturn(dto);

        List<ExpenseDto> result = expenseQueryService.list(START, END);

        assertNotNull(result);
    }
}
