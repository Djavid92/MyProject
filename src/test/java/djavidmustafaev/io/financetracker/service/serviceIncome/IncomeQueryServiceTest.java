package djavidmustafaev.io.financetracker.service.serviceIncome;

import djavidmustafaev.io.financetracker.dto.CategoryDto;
import djavidmustafaev.io.financetracker.dto.IncomeDto;
import djavidmustafaev.io.financetracker.mapper.IncomeExpenseMapper;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IncomeQueryServiceTest {

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private IncomeExpenseMapper mapper;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private IncomeQueryService service;

    @Test
    void list_shouldReturnMappedDtos() {
        LocalDate start = LocalDate.now().minusDays(5);
        LocalDate end = LocalDate.now();

        Income income = new Income();
        IncomeDto dto = new IncomeDto();

        when(incomeRepository.findIncomesByDateRange(start, end))
                .thenReturn(List.of(income));
        when(mapper.toIncomeDto(income)).thenReturn(dto);

        List<IncomeDto> result = service.list(start, end);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void totalForYearMonth_shouldReturnSum() {
        when(incomeRepository.sumAmountBetween(any(), any()))
                .thenReturn(BigDecimal.valueOf(100));

        BigDecimal result = service.totalForYearMonth(2024, 3);

        assertEquals(BigDecimal.valueOf(100), result);
    }

    @Test
    void listByCategory_shouldReturnMappedDtos() {
        CategoryDto categoryDto = CategoryDto.builder().name("Зарплата").build();
        Category category = new Category();
        Income income = new Income();
        IncomeDto dto = new IncomeDto();

        when(categoryRepository.findByName("Зарплата")).thenReturn(Optional.of(category));
        when(incomeRepository.findIncomeByCategory(category)).thenReturn(List.of(income));
        when(mapper.toIncomeDto(income)).thenReturn(dto);

        List<IncomeDto> result = service.list(categoryDto);

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