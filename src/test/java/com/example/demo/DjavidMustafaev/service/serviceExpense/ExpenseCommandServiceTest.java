package com.example.demo.DjavidMustafaev.service.serviceExpense;

import com.example.demo.DjavidMustafaev.dto.ExpenseDto;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseCommandServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ExpenseCommandService service;

    @Test
    void save_shouldSaveExpense() {
        ExpenseDto dto = new ExpenseDto();
        dto.setCategoryId(1L);
        dto.setDate(LocalDate.now());
        dto.setAmount(BigDecimal.TEN);

        Category category = new Category();

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        service.save(dto);

        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    void save_shouldThrowException_ifCategoryNotFound() {
        ExpenseDto dto = new ExpenseDto();
        dto.setCategoryId(1L);
        dto.setDate(LocalDate.now());

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.save(dto));
    }

    @Test
    void delete_shouldReturnTrue_ifExists() {
        when(expenseRepository.findById(1L))
                .thenReturn(Optional.of(new Expense()));

        boolean result = service.delete(1L);

        assertTrue(result);
        verify(expenseRepository).deleteById(1L);
    }

    @Test
    void delete_shouldReturnFalse_ifNotExists() {
        when(expenseRepository.findById(1L))
                .thenReturn(Optional.empty());

        boolean result = service.delete(1L);

        assertFalse(result);
        verify(expenseRepository, never()).deleteById(any());
    }

    @Test
    void deleteAll_shouldCallRepository() {
        service.deleteAll();

        verify(expenseRepository).deleteAll();
    }
}