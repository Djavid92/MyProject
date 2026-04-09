package djavidmustafaev.io.financetracker.repositories;

import djavidmustafaev.io.financetracker.model.Category;
import djavidmustafaev.io.financetracker.model.Expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class ExpenseRepositoryTest {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;

    @BeforeEach
    void setUp() {
        Category c = new Category();
        c.setName("TestCategory");
        category = categoryRepository.save(c);
    }

    private Expense buildExpense(String name, BigDecimal amount, LocalDate date) {
        return Expense.builder()
                .name(name)
                .category(category)
                .amount(amount)
                .description("test")
                .date(date)
                .build();
    }

    @Test
    void save_shouldPersistExpense() {
        Expense saved = expenseRepository.save(
                buildExpense("Groceries", BigDecimal.valueOf(100), LocalDate.of(2024, 3, 10)));

        assertNotNull(saved.getId());
        assertEquals("Groceries", saved.getName());
        assertEquals(category.getId(), saved.getCategory().getId());
    }

    @Test
    void findExpenseByDateRange_shouldReturnExpensesWithinRange() {
        expenseRepository.save(buildExpense("A", BigDecimal.valueOf(100), LocalDate.of(2024, 3, 10)));
        expenseRepository.save(buildExpense("B", BigDecimal.valueOf(200), LocalDate.of(2024, 3, 25)));
        expenseRepository.save(buildExpense("C", BigDecimal.valueOf(300), LocalDate.of(2024, 4, 5)));

        List<Expense> result = expenseRepository.findExpenseByDateRange(
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 31));

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(e -> e.getName().equals("A")));
        assertTrue(result.stream().anyMatch(e -> e.getName().equals("B")));
        assertFalse(result.stream().anyMatch(e -> e.getName().equals("C")));
    }

    @Test
    void findExpenseByDateRange_shouldIncludeBoundaryDates() {
        expenseRepository.save(buildExpense("Start", BigDecimal.valueOf(50), LocalDate.of(2024, 5, 1)));
        expenseRepository.save(buildExpense("End", BigDecimal.valueOf(50), LocalDate.of(2024, 5, 31)));

        List<Expense> result = expenseRepository.findExpenseByDateRange(
                LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 31));

        assertEquals(2, result.size());
    }

    @Test
    void findExpenseByDateRange_shouldReturnEmpty_whenNoExpensesInRange() {
        expenseRepository.save(buildExpense("Old", BigDecimal.valueOf(100), LocalDate.of(2023, 1, 10)));

        List<Expense> result = expenseRepository.findExpenseByDateRange(
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));

        assertTrue(result.isEmpty());
    }

    @Test
    void findExpenseByDateRange_shouldEagerlyFetchCategory() {
        expenseRepository.save(buildExpense("EagerTest", BigDecimal.valueOf(50), LocalDate.of(2024, 7, 1)));

        List<Expense> result = expenseRepository.findExpenseByDateRange(
                LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 31));

        assertFalse(result.isEmpty());
        assertNotNull(result.get(0).getCategory());
        assertEquals("TestCategory", result.get(0).getCategory().getName());
    }

    @Test
    void sumAmountBetween_shouldReturnCorrectSum() {
        expenseRepository.save(buildExpense("A", BigDecimal.valueOf(100), LocalDate.of(2024, 5, 1)));
        expenseRepository.save(buildExpense("B", BigDecimal.valueOf(200.50), LocalDate.of(2024, 5, 15)));
        expenseRepository.save(buildExpense("C_excluded", BigDecimal.valueOf(999), LocalDate.of(2024, 6, 1)));

        BigDecimal sum = expenseRepository.sumAmountBetween(
                LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 31));

        assertEquals(0, BigDecimal.valueOf(300.50).compareTo(sum));
    }

    @Test
    void sumAmountBetween_shouldReturnZero_whenNoExpenses() {
        BigDecimal sum = expenseRepository.sumAmountBetween(
                LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 31));

        assertEquals(0, BigDecimal.ZERO.compareTo(sum));
    }

    @Test
    void sumAmountBetween_shouldIncludeBoundaryDates() {
        expenseRepository.save(buildExpense("First", BigDecimal.valueOf(100), LocalDate.of(2024, 8, 1)));
        expenseRepository.save(buildExpense("Last", BigDecimal.valueOf(150), LocalDate.of(2024, 8, 31)));

        BigDecimal sum = expenseRepository.sumAmountBetween(
                LocalDate.of(2024, 8, 1), LocalDate.of(2024, 8, 31));

        assertEquals(0, BigDecimal.valueOf(250).compareTo(sum));
    }
}
