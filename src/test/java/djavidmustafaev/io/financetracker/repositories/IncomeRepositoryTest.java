package djavidmustafaev.io.financetracker.repositories;

import djavidmustafaev.io.financetracker.model.Category;
import djavidmustafaev.io.financetracker.model.Income;
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
class IncomeRepositoryTest {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;

    @BeforeEach
    void setUp() {
        Category c = new Category();
        c.setName("Salary");
        category = categoryRepository.save(c);
    }

    private Income buildIncome(String name, BigDecimal amount, LocalDate date) {
        return Income.builder()
                .name(name)
                .category(category)
                .amount(amount)
                .description("test")
                .date(date)
                .build();
    }

    @Test
    void save_shouldPersistIncome() {
        Income saved = incomeRepository.save(
                buildIncome("Monthly salary", BigDecimal.valueOf(5000), LocalDate.of(2024, 1, 5)));

        assertNotNull(saved.getId());
        assertEquals("Monthly salary", saved.getName());
        assertEquals(category.getId(), saved.getCategory().getId());
    }

    @Test
    void findIncomesByDateRange_shouldReturnIncomesWithinRange() {
        incomeRepository.save(buildIncome("Jan1", BigDecimal.valueOf(5000), LocalDate.of(2024, 1, 5)));
        incomeRepository.save(buildIncome("Jan2", BigDecimal.valueOf(1000), LocalDate.of(2024, 1, 20)));
        incomeRepository.save(buildIncome("Feb1", BigDecimal.valueOf(5000), LocalDate.of(2024, 2, 5)));

        List<Income> result = incomeRepository.findIncomesByDateRange(
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(i -> i.getName().equals("Jan1")));
        assertTrue(result.stream().anyMatch(i -> i.getName().equals("Jan2")));
    }

    @Test
    void findIncomesByDateRange_shouldIncludeBoundaryDates() {
        incomeRepository.save(buildIncome("Start", BigDecimal.valueOf(1000), LocalDate.of(2024, 3, 1)));
        incomeRepository.save(buildIncome("End", BigDecimal.valueOf(2000), LocalDate.of(2024, 3, 31)));

        List<Income> result = incomeRepository.findIncomesByDateRange(
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 31));

        assertEquals(2, result.size());
    }

    @Test
    void findIncomesByDateRange_shouldReturnEmpty_whenNoIncomesInRange() {
        incomeRepository.save(buildIncome("OldIncome", BigDecimal.valueOf(100), LocalDate.of(2023, 5, 10)));

        List<Income> result = incomeRepository.findIncomesByDateRange(
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));

        assertTrue(result.isEmpty());
    }

    @Test
    void findIncomesByDateRange_shouldEagerlyFetchCategory() {
        incomeRepository.save(buildIncome("EagerTest", BigDecimal.valueOf(3000), LocalDate.of(2024, 8, 1)));

        List<Income> result = incomeRepository.findIncomesByDateRange(
                LocalDate.of(2024, 8, 1), LocalDate.of(2024, 8, 31));

        assertFalse(result.isEmpty());
        assertNotNull(result.get(0).getCategory());
        assertEquals("Salary", result.get(0).getCategory().getName());
    }

    @Test
    void sumAmountBetween_shouldReturnCorrectSum() {
        incomeRepository.save(buildIncome("A", BigDecimal.valueOf(3000), LocalDate.of(2024, 6, 1)));
        incomeRepository.save(buildIncome("B", BigDecimal.valueOf(2000.75), LocalDate.of(2024, 6, 15)));
        incomeRepository.save(buildIncome("C_excluded", BigDecimal.valueOf(999), LocalDate.of(2024, 7, 1)));

        BigDecimal sum = incomeRepository.sumAmountBetween(
                LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 30));

        assertEquals(0, BigDecimal.valueOf(5000.75).compareTo(sum));
    }

    @Test
    void sumAmountBetween_shouldReturnZero_whenNoIncomes() {
        BigDecimal sum = incomeRepository.sumAmountBetween(
                LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 31));

        assertEquals(0, BigDecimal.ZERO.compareTo(sum));
    }

    @Test
    void sumAmountBetween_shouldIncludeBoundaryDates() {
        incomeRepository.save(buildIncome("First", BigDecimal.valueOf(1000), LocalDate.of(2024, 9, 1)));
        incomeRepository.save(buildIncome("Last", BigDecimal.valueOf(500), LocalDate.of(2024, 9, 30)));

        BigDecimal sum = incomeRepository.sumAmountBetween(
                LocalDate.of(2024, 9, 1), LocalDate.of(2024, 9, 30));

        assertEquals(0, BigDecimal.valueOf(1500).compareTo(sum));
    }
}
