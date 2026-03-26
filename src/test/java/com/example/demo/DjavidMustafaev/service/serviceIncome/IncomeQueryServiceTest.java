package com.example.demo.DjavidMustafaev.service.serviceIncome;

import com.example.demo.DjavidMustafaev.dto.IncomeDto;
import com.example.demo.DjavidMustafaev.mapper.IncomeExpenseMapper;
import com.example.demo.DjavidMustafaev.model.Income;
import com.example.demo.DjavidMustafaev.repositories.IncomeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IncomeQueryServiceTest {

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private IncomeExpenseMapper mapper;

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
}