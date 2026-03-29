package com.example.demo.DjavidMustafaev.service.serviceIncome;

import com.example.demo.DjavidMustafaev.dto.IncomeDto;
import com.example.demo.DjavidMustafaev.mapper.CategoryMapper;
import com.example.demo.DjavidMustafaev.mapper.IncomeExpenseMapper;
import com.example.demo.DjavidMustafaev.repositories.CategoryRepository;
import com.example.demo.DjavidMustafaev.repositories.ExpenseRepository;
import com.example.demo.DjavidMustafaev.repositories.IncomeRepository;
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
class IncomeQueryServiceCacheTest {

    @Autowired
    private IncomeQueryService incomeQueryService;

    @Autowired
    private CacheManager cacheManager;

    @MockBean
    private IncomeRepository incomeRepository;

    @MockBean
    private IncomeExpenseMapper incomeExpenseMapper;

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private ExpenseRepository expenseRepository;

    @MockBean
    private CategoryMapper categoryMapper;

    private static final LocalDate START = LocalDate.of(2024, 1, 1);
    private static final LocalDate END = LocalDate.of(2024, 1, 31);

    @BeforeEach
    void clearCaches() {
        cacheManager.getCacheNames().forEach(name ->
                Objects.requireNonNull(cacheManager.getCache(name)).clear());
    }

    @Test
    void list_shouldCacheByDateRange_andCallRepositoryOnlyOnce() {
        when(incomeRepository.findIncomesByDateRange(START, END)).thenReturn(List.of());

        incomeQueryService.list(START, END);
        incomeQueryService.list(START, END);
        incomeQueryService.list(START, END);

        verify(incomeRepository, times(1)).findIncomesByDateRange(START, END);
    }

    @Test
    void list_shouldUseSeparateCacheEntries_forDifferentDateRanges() {
        LocalDate start2 = LocalDate.of(2024, 2, 1);
        LocalDate end2 = LocalDate.of(2024, 2, 29);

        when(incomeRepository.findIncomesByDateRange(any(), any())).thenReturn(List.of());

        incomeQueryService.list(START, END);
        incomeQueryService.list(start2, end2);
        incomeQueryService.list(START, END);

        verify(incomeRepository, times(1)).findIncomesByDateRange(START, END);
        verify(incomeRepository, times(1)).findIncomesByDateRange(start2, end2);
    }

    @Test
    void totalForYearMonth_shouldCacheByYearAndMonth_andCallRepositoryOnlyOnce() {
        when(incomeRepository.sumAmountBetween(any(), any())).thenReturn(BigDecimal.valueOf(4000));

        BigDecimal first = incomeQueryService.totalForYearMonth(2024, 1);
        BigDecimal second = incomeQueryService.totalForYearMonth(2024, 1);

        assertEquals(0, BigDecimal.valueOf(4000).compareTo(first));
        assertEquals(first, second);
        verify(incomeRepository, times(1)).sumAmountBetween(any(), any());
    }

    @Test
    void totalForYearMonth_shouldUseSeparateCacheEntries_forDifferentMonths() {
        when(incomeRepository.sumAmountBetween(any(), any()))
                .thenReturn(BigDecimal.valueOf(4000), BigDecimal.valueOf(5000));

        incomeQueryService.totalForYearMonth(2024, 1);
        incomeQueryService.totalForYearMonth(2024, 2);
        incomeQueryService.totalForYearMonth(2024, 1);
        incomeQueryService.totalForYearMonth(2024, 2);

        verify(incomeRepository, times(2)).sumAmountBetween(any(), any());
    }

    @Test
    void totalForCurrentMonth_shouldCacheResult_andCallRepositoryOnlyOnce() {
        when(incomeRepository.sumAmountBetween(any(), any())).thenReturn(BigDecimal.valueOf(3000));

        BigDecimal first = incomeQueryService.totalForCurrentMonth();
        BigDecimal second = incomeQueryService.totalForCurrentMonth();

        assertEquals(first, second);
        verify(incomeRepository, times(1)).sumAmountBetween(any(), any());
    }

    @Test
    void list_shouldReturnMappedDtos() {
        IncomeDto dto = IncomeDto.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(200))
                .date(LocalDate.of(2024, 1, 15))
                .build();
        when(incomeRepository.findIncomesByDateRange(START, END)).thenReturn(List.of());
        when(incomeExpenseMapper.toIncomeDto(any())).thenReturn(dto);

        List<IncomeDto> result = incomeQueryService.list(START, END);

        assertNotNull(result);
    }
}
