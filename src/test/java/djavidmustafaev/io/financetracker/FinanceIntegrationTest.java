package djavidmustafaev.io.financetracker;

import djavidmustafaev.io.financetracker.model.Category;
import djavidmustafaev.io.financetracker.model.Expense;
import djavidmustafaev.io.financetracker.model.Income;
import djavidmustafaev.io.financetracker.repositories.CategoryRepository;
import djavidmustafaev.io.financetracker.repositories.ExpenseRepository;
import djavidmustafaev.io.financetracker.repositories.IncomeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FinanceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private CacheManager cacheManager;

    private Category testCategory;
    private static final LocalDate TEST_DATE = LocalDate.now().minusDays(1);

    @BeforeEach
    void setUp() {
        expenseRepository.deleteAll();
        incomeRepository.deleteAll();
        categoryRepository.deleteAll();
        cacheManager.getCacheNames().forEach(name ->
                Objects.requireNonNull(cacheManager.getCache(name)).clear());

        Category category = new Category();
        category.setName("TestCategory");
        testCategory = categoryRepository.save(category);
    }

    @AfterEach
    void tearDown() {
        expenseRepository.deleteAll();
        incomeRepository.deleteAll();
        categoryRepository.deleteAll();
        cacheManager.getCacheNames().forEach(name ->
                Objects.requireNonNull(cacheManager.getCache(name)).clear());
    }

    // ─── Category endpoints ──────────────────────────────────────────────────

    @Test
    void addCategory_shouldReturn201() throws Exception {
        mockMvc.perform(post("/api/dashboard/addCategory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"NewCategory\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Категория")));
    }

    @Test
    void listCategories_shouldReturnExistingCategory() throws Exception {
        mockMvc.perform(get("/api/dashboard/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == 'TestCategory')]").exists());
    }

    @Test
    void deleteCategory_shouldReturn204_whenExists() throws Exception {
        mockMvc.perform(delete("/api/dashboard/category/{id}", testCategory.getId()))
                .andExpect(status().isNoContent());

        assertFalse(categoryRepository.findById(testCategory.getId()).isPresent());
    }

    @Test
    void deleteCategory_shouldReturn404_whenNotExists() throws Exception {
        mockMvc.perform(delete("/api/dashboard/category/{id}", 999999L))
                .andExpect(status().isNotFound());
    }

    // ─── Income endpoints ────────────────────────────────────────────────────

    @Test
    void addIncome_shouldReturn201_andPersistToDatabase() throws Exception {
        String json = String.format(
                "{\"name\":\"Salary\",\"categoryId\":%d,\"amount\":5000,\"date\":\"%s\"}",
                testCategory.getId(), TEST_DATE);

        mockMvc.perform(post("/api/dashboard/addIncome")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        assertEquals(1, incomeRepository.findAll().size());
        assertEquals("Salary", incomeRepository.findAll().get(0).getName());
    }

    @Test
    void updateIncome_shouldReturn204_andUpdateRecord() throws Exception {
        Income income = incomeRepository.save(Income.builder()
                .name("OldName")
                .category(testCategory)
                .amount(BigDecimal.valueOf(1000))
                .description("old")
                .date(TEST_DATE)
                .build());

        String json = String.format(
                "{\"name\":\"NewName\",\"categoryId\":%d,\"amount\":2000,\"date\":\"%s\"}",
                testCategory.getId(), TEST_DATE);

        mockMvc.perform(put("/api/dashboard/incomes/{id}", income.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());

        Income updated = incomeRepository.findById(income.getId()).orElseThrow();
        assertEquals("NewName", updated.getName());
        assertEquals(0, BigDecimal.valueOf(2000).compareTo(updated.getAmount()));
    }

    @Test
    void updateIncome_shouldReturn404_whenNotExists() throws Exception {
        String json = String.format(
                "{\"name\":\"X\",\"categoryId\":%d,\"amount\":100,\"date\":\"%s\"}",
                testCategory.getId(), TEST_DATE);

        mockMvc.perform(put("/api/dashboard/incomes/{id}", 999999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteIncome_shouldReturn204_andRemoveRecord() throws Exception {
        Income income = incomeRepository.save(Income.builder()
                .name("ToDelete")
                .category(testCategory)
                .amount(BigDecimal.valueOf(500))
                .date(TEST_DATE)
                .build());

        mockMvc.perform(delete("/api/dashboard/incomes/{id}", income.getId()))
                .andExpect(status().isNoContent());

        assertFalse(incomeRepository.findById(income.getId()).isPresent());
    }

    @Test
    void deleteIncome_shouldReturn404_whenNotExists() throws Exception {
        mockMvc.perform(delete("/api/dashboard/incomes/{id}", 999999L))
                .andExpect(status().isNotFound());
    }

    // ─── Expense endpoints ───────────────────────────────────────────────────

    @Test
    void addExpense_shouldReturn201_andPersistToDatabase() throws Exception {
        String json = String.format(
                "{\"name\":\"Groceries\",\"categoryId\":%d,\"amount\":300,\"date\":\"%s\"}",
                testCategory.getId(), TEST_DATE);

        mockMvc.perform(post("/api/dashboard/addExpense")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        assertEquals(1, expenseRepository.findAll().size());
    }

    @Test
    void updateExpense_shouldReturn204_andUpdateRecord() throws Exception {
        Expense expense = expenseRepository.save(Expense.builder()
                .name("OldExpense")
                .category(testCategory)
                .amount(BigDecimal.valueOf(200))
                .description("old")
                .date(TEST_DATE)
                .build());

        String json = String.format(
                "{\"name\":\"NewExpense\",\"categoryId\":%d,\"amount\":400,\"date\":\"%s\"}",
                testCategory.getId(), TEST_DATE);

        mockMvc.perform(put("/api/dashboard/expenses/{id}", expense.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());

        Expense updated = expenseRepository.findById(expense.getId()).orElseThrow();
        assertEquals("NewExpense", updated.getName());
        assertEquals(0, BigDecimal.valueOf(400).compareTo(updated.getAmount()));
    }

    @Test
    void updateExpense_shouldReturn404_whenNotExists() throws Exception {
        String json = String.format(
                "{\"name\":\"X\",\"categoryId\":%d,\"amount\":100,\"date\":\"%s\"}",
                testCategory.getId(), TEST_DATE);

        mockMvc.perform(put("/api/dashboard/expenses/{id}", 999999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteExpense_shouldReturn204_andRemoveRecord() throws Exception {
        Expense expense = expenseRepository.save(Expense.builder()
                .name("ToDelete")
                .category(testCategory)
                .amount(BigDecimal.valueOf(100))
                .date(TEST_DATE)
                .build());

        mockMvc.perform(delete("/api/dashboard/expenses/{id}", expense.getId()))
                .andExpect(status().isNoContent());

        assertFalse(expenseRepository.findById(expense.getId()).isPresent());
    }

    @Test
    void deleteExpense_shouldReturn404_whenNotExists() throws Exception {
        mockMvc.perform(delete("/api/dashboard/expenses/{id}", 999999L))
                .andExpect(status().isNotFound());
    }

    // ─── Dashboard / totals endpoints ────────────────────────────────────────

    @Test
    void getTotals_shouldReturn200_withIncomeAndExpenseKeys() throws Exception {
        mockMvc.perform(get("/api/dashboard/totals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.income").exists())
                .andExpect(jsonPath("$.expense").exists());
    }

    @Test
    void getTotals_shouldReflectSavedIncomeForCurrentMonth() throws Exception {
        LocalDate currentMonthDate = LocalDate.now().withDayOfMonth(1);
        incomeRepository.save(Income.builder()
                .name("CurrentMonthIncome")
                .category(testCategory)
                .amount(BigDecimal.valueOf(7000))
                .date(currentMonthDate)
                .build());

        cacheManager.getCacheNames().forEach(name ->
                Objects.requireNonNull(cacheManager.getCache(name)).clear());

        MvcResult result = mockMvc.perform(get("/api/dashboard/totals"))
                .andExpect(status().isOk())
                .andReturn();

        Map<?, ?> response = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        BigDecimal income = new BigDecimal(response.get("income").toString());
        assertTrue(income.compareTo(BigDecimal.valueOf(7000)) >= 0);
    }

    @Test
    void getTotalsByMonth_shouldReturn200_withCorrectTotals() throws Exception {
        incomeRepository.save(Income.builder()
                .name("IncomeJan")
                .category(testCategory)
                .amount(BigDecimal.valueOf(3000))
                .date(LocalDate.of(2023, 6, 10))
                .build());
        expenseRepository.save(Expense.builder()
                .name("ExpenseJan")
                .category(testCategory)
                .amount(BigDecimal.valueOf(1500))
                .date(LocalDate.of(2023, 6, 10))
                .build());

        cacheManager.getCacheNames().forEach(name ->
                Objects.requireNonNull(cacheManager.getCache(name)).clear());

        MvcResult result = mockMvc.perform(get("/api/dashboard/totals/by-month")
                        .param("year", "2023")
                        .param("month", "6"))
                .andExpect(status().isOk())
                .andReturn();

        Map<?, ?> response = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        BigDecimal income = new BigDecimal(response.get("income").toString());
        BigDecimal expense = new BigDecimal(response.get("expense").toString());
        assertEquals(0, BigDecimal.valueOf(3000).compareTo(income));
        assertEquals(0, BigDecimal.valueOf(1500).compareTo(expense));
    }

    @Test
    void getOperationsByMonth_shouldReturn200_withIncomesAndExpenses() throws Exception {
        incomeRepository.save(Income.builder()
                .name("SomeIncome")
                .category(testCategory)
                .amount(BigDecimal.valueOf(500))
                .date(LocalDate.of(2023, 7, 15))
                .build());
        expenseRepository.save(Expense.builder()
                .name("SomeExpense")
                .category(testCategory)
                .amount(BigDecimal.valueOf(200))
                .date(LocalDate.of(2023, 7, 20))
                .build());

        cacheManager.getCacheNames().forEach(name ->
                Objects.requireNonNull(cacheManager.getCache(name)).clear());

        mockMvc.perform(get("/api/dashboard/operations/by-month")
                        .param("year", "2023")
                        .param("month", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.incomes").isArray())
                .andExpect(jsonPath("$.expenses").isArray())
                .andExpect(jsonPath("$.incomes[0].name").value("SomeIncome"))
                .andExpect(jsonPath("$.expenses[0].name").value("SomeExpense"))
                .andExpect(jsonPath("$.startDate").value("2023-07-01"))
                .andExpect(jsonPath("$.endDate").value("2023-07-31"));
    }

    @Test
    void getOperationsByMonth_shouldReturnEmptyLists_whenNoData() throws Exception {
        mockMvc.perform(get("/api/dashboard/operations/by-month")
                        .param("year", "2000")
                        .param("month", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.incomes").isArray())
                .andExpect(jsonPath("$.expenses").isArray())
                .andExpect(jsonPath("$.incomes", hasSize(0)))
                .andExpect(jsonPath("$.expenses", hasSize(0)));
    }
}
