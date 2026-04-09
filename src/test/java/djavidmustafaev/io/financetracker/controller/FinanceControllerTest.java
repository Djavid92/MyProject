package djavidmustafaev.io.financetracker.controller;

import djavidmustafaev.io.financetracker.dto.CategoryDto;
import djavidmustafaev.io.financetracker.dto.ExpenseDto;
import djavidmustafaev.io.financetracker.dto.IncomeDto;
import djavidmustafaev.io.financetracker.service.serviceCategory.CategoryService;
import djavidmustafaev.io.financetracker.service.serviceExpense.FinanceFacadeExpense;
import djavidmustafaev.io.financetracker.service.serviceIncome.FinanceFacadeIncome;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebMvcTest(FinanceController.class)
class FinanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FinanceFacadeIncome incomeService;

    @Autowired
    private FinanceFacadeExpense expenseService;

    @Autowired
    private CategoryService categoryService;

    @TestConfiguration
    static class MockConfig {

        @Bean
        public FinanceFacadeIncome incomeService() {
            return mock(FinanceFacadeIncome.class);
        }

        @Bean
        public FinanceFacadeExpense expenseService() {
            return mock(FinanceFacadeExpense.class);
        }

        @Bean
        public CategoryService categoryService() {
            return mock(CategoryService.class);
        }
    }

    // 🔹 0. GET /calculate

    @Test
    void calculate_shouldReturnSum() throws Exception {
        mockMvc.perform(get("/api/dashboard/calculate")
                        .param("numbers", "100", "50")
                        .param("operator", "+"))
                .andExpect(status().isOk())
                .andExpect(content().string("150"));
    }

    @Test
    void calculate_shouldReturnDifference() throws Exception {
        mockMvc.perform(get("/api/dashboard/calculate")
                        .param("numbers", "100", "50")
                        .param("operator", "-"))
                .andExpect(status().isOk())
                .andExpect(content().string("50"));
    }

    @Test
    void calculate_shouldReturnProduct() throws Exception {
        mockMvc.perform(get("/api/dashboard/calculate")
                        .param("numbers", "10", "5")
                        .param("operator", "*"))
                .andExpect(status().isOk())
                .andExpect(content().string("50"));
    }

    @Test
    void calculate_shouldReturnQuotient() throws Exception {
        mockMvc.perform(get("/api/dashboard/calculate")
                        .param("numbers", "100", "4")
                        .param("operator", "/"))
                .andExpect(status().isOk())
                .andExpect(content().string("25.00"));
    }

    @Test
    void calculate_shouldReturn400_onDivisionByZero() throws Exception {
        mockMvc.perform(get("/api/dashboard/calculate")
                        .param("numbers", "100", "0")
                        .param("operator", "/"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Деление на ноль"));
    }

    @Test
    void calculate_shouldReturn400_onUnknownOperator() throws Exception {
        mockMvc.perform(get("/api/dashboard/calculate")
                        .param("numbers", "100", "50")
                        .param("operator", "%"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    // 🔹 1. GET /totals

    @Test
    void getTotals_shouldReturnIncomeAndExpense() throws Exception {
        when(incomeService.totalIncomeForCurrentMonth())
                .thenReturn(BigDecimal.valueOf(1000));
        when(expenseService.totalExpenseForCurrentMonth())
                .thenReturn(BigDecimal.valueOf(500));

        mockMvc.perform(get("/api/dashboard/totals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.income").value(1000))
                .andExpect(jsonPath("$.expense").value(500));
    }

    // 🔹 2. GET /totals/by-month

    @Test
    void getTotalsByMonth_shouldReturnData() throws Exception {
        when(incomeService.totalIncomesFor(2024, 3))
                .thenReturn(BigDecimal.valueOf(2000));
        when(expenseService.totalExpensesFor(2024, 3))
                .thenReturn(BigDecimal.valueOf(800));

        mockMvc.perform(get("/api/dashboard/totals/by-month")
                        .param("year", "2024")
                        .param("month", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.income").value(2000))
                .andExpect(jsonPath("$.expense").value(800));
    }

    // 🔹 3a. GET /operations/by-categoryIncome

    @Test
    void getOperationByIncomeCategory_shouldReturnList() throws Exception {
        List<IncomeDto> incomes = List.of(new IncomeDto());

        when(incomeService.listIncome(any(CategoryDto.class))).thenReturn(incomes);

        mockMvc.perform(get("/api/dashboard/operations/by-categoryIncome")
                        .param("name", "Зарплата"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getOperationByIncomeCategory_shouldReturnEmptyList_whenNoOperations() throws Exception {
        when(incomeService.listIncome(any(CategoryDto.class))).thenReturn(List.of());

        mockMvc.perform(get("/api/dashboard/operations/by-categoryIncome")
                        .param("name", "Зарплата"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // 🔹 3b. GET /operations/by-categoryExpense

    @Test
    void getOperationByExpenseCategory_shouldReturnList() throws Exception {
        List<ExpenseDto> expenses = List.of(new ExpenseDto());

        when(expenseService.listExpenses(any(CategoryDto.class))).thenReturn(expenses);

        mockMvc.perform(get("/api/dashboard/operations/by-categoryExpense")
                        .param("name", "Продукты"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getOperationByExpenseCategory_shouldReturnEmptyList_whenNoOperations() throws Exception {
        when(expenseService.listExpenses(any(CategoryDto.class))).thenReturn(List.of());

        mockMvc.perform(get("/api/dashboard/operations/by-categoryExpense")
                        .param("name", "Продукты"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // 🔹 3. GET /operations/by-month

    @Test
    void getOperationsByMonth_shouldReturnLists() throws Exception {
        List<IncomeDto> incomes = List.of(new IncomeDto());
        List<ExpenseDto> expenses = List.of(new ExpenseDto());

        when(incomeService.listIncome(any(), any())).thenReturn(incomes);
        when(expenseService.listExpenses(any(), any())).thenReturn(expenses);

        mockMvc.perform(get("/api/dashboard/operations/by-month")
                        .param("year", "2024")
                        .param("month", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.incomes.length()").value(1))
                .andExpect(jsonPath("$.expenses.length()").value(1))
                .andExpect(jsonPath("$.startDate").exists())
                .andExpect(jsonPath("$.endDate").exists());
    }

    // 🔹 4. POST /addIncome

    @Test
    void addIncome_shouldReturn201() throws Exception {
        String json = """
                {
                  "amount": 100,
                  "date": "2024-03-10",
                  "categoryId": 1
                }
                """;

        mockMvc.perform(post("/api/dashboard/addIncome")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        verify(incomeService).addIncome(any());
    }

    // 🔹 5. POST /addExpense

    @Test
    void addExpense_shouldReturn201() throws Exception {
        String json = """
                {
                  "amount": 200,
                  "date": "2024-03-10",
                  "categoryId": 1
                }
                """;

        mockMvc.perform(post("/api/dashboard/addExpense")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        verify(expenseService).addExpense(any());
    }

    // 🔹 6. PUT /incomes/{id}

    @Test
    void updateIncome_shouldReturn204_ifUpdated() throws Exception {
        String json = """
                {
                  "amount": 150,
                  "date": "2024-03-10",
                  "categoryId": 1
                }
                """;

        when(incomeService.updateIncome(eq(1L), any())).thenReturn(true);

        mockMvc.perform(put("/api/dashboard/incomes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateIncome_shouldReturn404_ifNotFound() throws Exception {
        String json = """
                {
                  "amount": 150,
                  "date": "2024-03-10",
                  "categoryId": 1
                }
                """;

        when(incomeService.updateIncome(eq(1L), any())).thenReturn(false);

        mockMvc.perform(put("/api/dashboard/incomes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    // 🔹 7. PUT /expenses/{id}

    @Test
    void updateExpense_shouldReturn204_ifUpdated() throws Exception {
        String json = """
                {
                  "amount": 250,
                  "date": "2024-03-10",
                  "categoryId": 1
                }
                """;

        when(expenseService.updateExpense(eq(1L), any())).thenReturn(true);

        mockMvc.perform(put("/api/dashboard/expenses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateExpense_shouldReturn404_ifNotFound() throws Exception {
        String json = """
                {
                  "amount": 250,
                  "date": "2024-03-10",
                  "categoryId": 1
                }
                """;

        when(expenseService.updateExpense(eq(1L), any())).thenReturn(false);

        mockMvc.perform(put("/api/dashboard/expenses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    // 🔹 8. DELETE /incomes/{id}

    @Test
    void deleteIncome_shouldReturn204_ifDeleted() throws Exception {
        when(incomeService.deleteIncome(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/dashboard/incomes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteIncome_shouldReturn404_ifNotFound() throws Exception {
        when(incomeService.deleteIncome(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/dashboard/incomes/1"))
                .andExpect(status().isNotFound());
    }

    // 🔹 8. DELETE /expenses/{id}

    @Test
    void deleteExpense_shouldReturn204_ifDeleted() throws Exception {
        when(expenseService.deleteExpense(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/dashboard/expenses/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteExpense_shouldReturn404_ifNotFound() throws Exception {
        when(expenseService.deleteExpense(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/dashboard/expenses/1"))
                .andExpect(status().isNotFound());
    }

    // 🔹 9. POST /addCategory

    @Test
    void createCategory_shouldReturn201() throws Exception {
        String json = """
                {
                  "name": "Food"
                }
                """;

        mockMvc.perform(post("/api/dashboard/addCategory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        verify(categoryService).create(any());
    }

    // 🔹 10. GET /categories

    @Test
    void getCategories_shouldReturnList() throws Exception {
        when(categoryService.getAll()).thenReturn(List.of(new CategoryDto()));

        mockMvc.perform(get("/api/dashboard/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // 🔹 11. DELETE /category/{id}

    @Test
    void deleteCategory_shouldReturn204_ifDeleted() throws Exception {
        when(categoryService.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/dashboard/category/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCategory_shouldReturn404_ifNotFound() throws Exception {
        when(categoryService.delete(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/dashboard/category/1"))
                .andExpect(status().isNotFound());
    }
}