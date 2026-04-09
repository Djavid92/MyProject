package djavidmustafaev.io.financetracker.mapper;

import djavidmustafaev.io.financetracker.dto.ExpenseDto;
import djavidmustafaev.io.financetracker.dto.IncomeDto;
import djavidmustafaev.io.financetracker.model.Category;
import djavidmustafaev.io.financetracker.model.Expense;
import djavidmustafaev.io.financetracker.model.Income;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = IncomeExpenseMapperImpl.class)
class IncomeExpenseMapperTest {

    @Autowired
    private IncomeExpenseMapper mapper;

    private Category buildCategory(Long id, String name) {
        Category c = new Category();
        c.setId(id);
        c.setName(name);
        return c;
    }

    @Test
    void toExpenseDto_shouldMapAllFields() {
        Expense expense = Expense.builder()
                .id(1L)
                .name("Groceries")
                .category(buildCategory(5L, "Food"))
                .amount(BigDecimal.valueOf(150))
                .description("Weekly groceries")
                .date(LocalDate.of(2024, 3, 10))
                .build();

        ExpenseDto dto = mapper.toExpenseDto(expense);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Groceries", dto.getName());
        assertEquals(5L, dto.getCategoryId());
        assertNotNull(dto.getCategory());
        assertEquals("Food", dto.getCategory().getName());
        assertEquals(0, BigDecimal.valueOf(150).compareTo(dto.getAmount()));
        assertEquals("Weekly groceries", dto.getDescription());
        assertEquals(LocalDate.of(2024, 3, 10), dto.getDate());
    }

    @Test
    void toExpenseDto_shouldReturnNull_whenInputIsNull() {
        assertNull(mapper.toExpenseDto(null));
    }

    @Test
    void toExpenseDto_shouldMapNullCategoryGracefully() {
        Expense expense = Expense.builder()
                .id(2L)
                .name("Unknown")
                .category(null)
                .amount(BigDecimal.TEN)
                .date(LocalDate.of(2024, 1, 1))
                .build();

        ExpenseDto dto = mapper.toExpenseDto(expense);

        assertNotNull(dto);
        assertNull(dto.getCategory());
        assertNull(dto.getCategoryId());
    }

    @Test
    void toIncomeDto_shouldMapAllFields() {
        Income income = Income.builder()
                .id(10L)
                .name("Monthly salary")
                .category(buildCategory(3L, "Salary"))
                .amount(BigDecimal.valueOf(5000))
                .description("November salary")
                .date(LocalDate.of(2024, 11, 1))
                .build();

        IncomeDto dto = mapper.toIncomeDto(income);

        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals("Monthly salary", dto.getName());
        assertEquals(3L, dto.getCategoryId());
        assertNotNull(dto.getCategory());
        assertEquals("Salary", dto.getCategory().getName());
        assertEquals(0, BigDecimal.valueOf(5000).compareTo(dto.getAmount()));
        assertEquals("November salary", dto.getDescription());
        assertEquals(LocalDate.of(2024, 11, 1), dto.getDate());
    }

    @Test
    void toIncomeDto_shouldReturnNull_whenInputIsNull() {
        assertNull(mapper.toIncomeDto(null));
    }

    @Test
    void toExpenseEntity_shouldMapBasicFields() {
        ExpenseDto dto = ExpenseDto.builder()
                .name("Rent")
                .amount(BigDecimal.valueOf(1200))
                .description("Monthly rent")
                .date(LocalDate.of(2024, 1, 1))
                .build();

        Expense expense = mapper.toExpenseEntity(dto);

        assertNotNull(expense);
        assertEquals("Rent", expense.getName());
        assertEquals(0, BigDecimal.valueOf(1200).compareTo(expense.getAmount()));
        assertEquals("Monthly rent", expense.getDescription());
        assertEquals(LocalDate.of(2024, 1, 1), expense.getDate());
    }

    @Test
    void toIncomeEntity_shouldMapBasicFields() {
        IncomeDto dto = IncomeDto.builder()
                .name("Freelance")
                .amount(BigDecimal.valueOf(800))
                .description("Side project")
                .date(LocalDate.of(2024, 3, 10))
                .build();

        Income income = mapper.toIncomeEntity(dto);

        assertNotNull(income);
        assertEquals("Freelance", income.getName());
        assertEquals(0, BigDecimal.valueOf(800).compareTo(income.getAmount()));
        assertEquals("Side project", income.getDescription());
        assertEquals(LocalDate.of(2024, 3, 10), income.getDate());
    }
}
