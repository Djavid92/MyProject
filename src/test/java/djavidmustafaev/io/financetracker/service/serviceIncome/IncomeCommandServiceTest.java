package djavidmustafaev.io.financetracker.service.serviceIncome;

import djavidmustafaev.io.financetracker.dto.IncomeDto;
import djavidmustafaev.io.financetracker.model.Category;
import djavidmustafaev.io.financetracker.model.Income;
import djavidmustafaev.io.financetracker.repositories.CategoryRepository;
import djavidmustafaev.io.financetracker.repositories.IncomeRepository;
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
class IncomeCommandServiceTest {

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private IncomeCommandService service;

    @Test
    void save_shouldSaveIncome() {
        IncomeDto dto = new IncomeDto();
        dto.setCategoryId(1L);
        dto.setDate(LocalDate.now());
        dto.setAmount(BigDecimal.TEN);

        Category category = new Category();
        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        service.save(dto);

        verify(incomeRepository).save(any(Income.class));
    }

    @Test
    void save_shouldThrowException_ifCategoryNotFound() {
        IncomeDto dto = new IncomeDto();
        dto.setCategoryId(1L);
        dto.setDate(LocalDate.now());

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.save(dto));
    }

    @Test
    void delete_shouldReturnTrue_ifExists() {
        when(incomeRepository.findById(1L))
                .thenReturn(Optional.of(new Income()));

        boolean result = service.delete(1L);

        assertTrue(result);
        verify(incomeRepository).deleteById(1L);
    }

    @Test
    void delete_shouldReturnFalse_ifNotExists() {
        when(incomeRepository.findById(1L))
                .thenReturn(Optional.empty());

        boolean result = service.delete(1L);

        assertFalse(result);
        verify(incomeRepository, never()).deleteById(any());
    }

    @Test
    void update_shouldUpdateAndReturnTrue_ifExists() {
        Income existing = new Income();
        IncomeDto dto = new IncomeDto();
        dto.setCategoryId(1L);
        dto.setDate(LocalDate.now());
        dto.setAmount(BigDecimal.valueOf(200));
        dto.setName("Updated");

        when(incomeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category()));

        boolean result = service.update(1L, dto);

        assertTrue(result);
        verify(incomeRepository).save(existing);
    }

    @Test
    void update_shouldReturnFalse_ifNotExists() {
        IncomeDto dto = new IncomeDto();
        dto.setCategoryId(1L);
        dto.setDate(LocalDate.now());
        dto.setAmount(BigDecimal.TEN);

        when(incomeRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = service.update(1L, dto);

        assertFalse(result);
        verify(incomeRepository, never()).save(any());
    }

    @Test
    void update_shouldThrowException_ifCategoryNotFound() {
        IncomeDto dto = new IncomeDto();
        dto.setCategoryId(1L);
        dto.setDate(LocalDate.now());
        dto.setAmount(BigDecimal.TEN);

        when(incomeRepository.findById(1L)).thenReturn(Optional.of(new Income()));
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.update(1L, dto));
    }

}