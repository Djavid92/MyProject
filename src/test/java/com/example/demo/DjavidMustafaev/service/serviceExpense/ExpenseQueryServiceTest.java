package com.example.demo.DjavidMustafaev.service.serviceExpense;

import com.example.demo.DjavidMustafaev.dto.CategoryDto;
import com.example.demo.DjavidMustafaev.dto.ExpenseDto;
import com.example.demo.DjavidMustafaev.mapper.IncomeExpenseMapper;
import com.example.demo.DjavidMustafaev.model.Category;
import com.example.demo.DjavidMustafaev.model.Expense;
import com.example.demo.DjavidMustafaev.repositories.CategoryRepository;
import com.example.demo.DjavidMustafaev.repositories.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseQueryServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private IncomeExpenseMapper mapper;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ExpenseQueryService service;

    @Test
    void list_shouldReturnMappedDtos() {
        LocalDate start = LocalDate.now().minusDays(3);
        LocalDate end = LocalDate.now();

        Expense expense = new Expense();
        ExpenseDto dto = new ExpenseDto();

        when(expenseRepository.findExpenseByDateRange(start, end))
                .thenReturn(List.of(expense));
        when(mapper.toExpenseDto(expense))
                .thenReturn(dto);

        List<ExpenseDto> result = service.list(start, end);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void totalForYearMonth_shouldCallRepository() {
        when(expenseRepository.sumAmountBetween(any(), any()))
                .thenReturn(BigDecimal.valueOf(200));

        BigDecimal result = service.totalForYearMonth(2024, 3);

        assertEquals(BigDecimal.valueOf(200), result);
    }

    @Test
    void listByCategory_shouldReturnMappedDtos() {
        CategoryDto categoryDto = CategoryDto.builder().name("Продукты").build();
        Category category = new Category();
        Expense expense = new Expense();
        ExpenseDto dto = new ExpenseDto();

        when(categoryRepository.findByName("Продукты")).thenReturn(Optional.of(category));
        when(expenseRepository.findIncomeByCategory(category)).thenReturn(List.of(expense));
        when(mapper.toExpenseDto(expense)).thenReturn(dto);

        List<ExpenseDto> result = service.list(categoryDto);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void listByCategory_shouldThrow_whenCategoryNotFound() {
        CategoryDto categoryDto = CategoryDto.builder().name("Несуществующая").build();

        when(categoryRepository.findByName("Несуществующая")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.list(categoryDto));
    }
}