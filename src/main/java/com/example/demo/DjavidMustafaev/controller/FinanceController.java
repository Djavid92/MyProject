package com.example.demo.DjavidMustafaev.controller;

import com.example.demo.DjavidMustafaev.dto.CategoryDto;
import com.example.demo.DjavidMustafaev.dto.ExpenseDto;
import com.example.demo.DjavidMustafaev.dto.IncomeDto;
import com.example.demo.DjavidMustafaev.service.serviceCategory.CategoryService;
import com.example.demo.DjavidMustafaev.service.serviceExpense.FinanceFacadeExpense;
import com.example.demo.DjavidMustafaev.service.serviceIncome.FinanceFacadeIncome;
import com.example.demo.DjavidMustafaev.util.Util;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "main_methods")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceFacadeIncome financeFacadeIncome;
    private final FinanceFacadeExpense financeFacadeExpense;
    private final CategoryService categoryService;

    @Operation(
            summary = "Получает общую сумму доходов и расходов за текущий месяц",
            description = "Содержит в себе 2 метода из сервиса которые принимают в качестве параметра текущую дату и"
                    + " " + "выдают сумму расходов и доходов отдельно за текущий месяц"
    )
    @GetMapping("/totals") // сумма доходов и расходов за текущий месяц
    public ResponseEntity<Map<String, BigDecimal>> getTotalIncomeAndExpense() {
        BigDecimal totalIncome = financeFacadeIncome.totalIncomeForCurrentMonth();
        BigDecimal totalExpense = financeFacadeExpense.totalExpenseForCurrentMonth();

        Map<String, BigDecimal> response = new HashMap<>();
        response.put("income", totalIncome);
        response.put("expense", totalExpense);
        return ResponseEntity.ok().body(response);
    }

    @Operation(
            summary = "Получает общую сумму доходов и расходов за произвольный месяц",
            description = "Принимает в качестве параметра год и месяц и выдает сумму доходов расходов за произвольный месяц"
    )
    @GetMapping("/totals/by-month") // сумма доходов и расходов за остальные месяца
    public ResponseEntity<Map<String, BigDecimal>> getTotalsByMonth(@RequestParam("year") int year,
                                                                    @RequestParam("month") int month) {
        Map<String, BigDecimal> response = new HashMap<>();
        response.put("income", financeFacadeIncome.totalIncomesFor(year, month));
        response.put("expense", financeFacadeExpense.totalExpensesFor(year, month));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Получает операции доходов и расходов за любой месяц кроме текущего",
            description = "Принимает в качестве параметра год и месяц и выдает операции доходов расходов" + " " +
                    "за произвольный месяц"
    )
    @GetMapping("/operations/by-month") // операции за любой месяц
    public ResponseEntity<Map<String, Object>> getOperationByMonth(@RequestParam("year") int year,
                                                                   @RequestParam("month") int month) {
        Map<String, LocalDate> mapDate = Util.getStartAndEndDate(year, month);
        List<IncomeDto> incomes = financeFacadeIncome.listIncome(mapDate.get("startDate"), mapDate.get("endDate"));
        List<ExpenseDto> expenses = financeFacadeExpense.listExpenses(mapDate.get("startDate"), mapDate.get("endDate"));

        Map<String, Object> response = new HashMap<>();
        response.put("incomes", incomes);
        response.put("expenses", expenses);
        response.put("startDate", mapDate.get("startDate").toString());
        response.put("endDate", mapDate.get("endDate").toString());
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Добавляет новую операцию дохода в базу",
            description = "Получает DTO дохода, ищет подходящую категорию в таблице," + " " +
                    "билдером присваивает к сущности" + " " +
                    "и сохраняет в базу"
    )
    @PostMapping("/addIncome") // добавление дохода
    public ResponseEntity<String> addIncome(@RequestBody IncomeDto incomeDto) {
        financeFacadeIncome.addIncome(incomeDto);
        return ResponseEntity.status(201).body("Доход успешно добавлен");
    }

    @Operation(
            summary = "Добавляет новую операцию расхода в базу",
            description = "Получает DTO расхода, ищет подходящую категорию в таблице," + " " +
                    "билдером присваивает к сущности" + " " +
                    "и сохраняет в базу"
    )
    @PostMapping("/addExpense") // добавление расхода
    public ResponseEntity<String> addExpense(@RequestBody ExpenseDto expenseDto) {
        financeFacadeExpense.addExpense(expenseDto);
        return ResponseEntity.status(201).body("Расход успешно добавлен");
    }

    @Operation(
            summary = "Удаление всех операций",
            description = "Удаление доходов и расходов из базы данных за все периоды"
    )
    @DeleteMapping("/delete/all") // удалить все
    public ResponseEntity<String> deleteAll() {
        financeFacadeIncome.deleteAll();
        financeFacadeExpense.deleteAll();
        return ResponseEntity.ok("Все успешно удалилось");
    }

    @Operation(
            summary = "Удаление дохода",
            description = "Удаление конкретного дохода по id из базы данных"
    )
    @DeleteMapping("/incomes/{id}") // удалить конкретный доход операцию
    public ResponseEntity<Void> deleteIncomeOperation(@PathVariable("id") Long id) {
        boolean deleted = financeFacadeIncome.deleteIncome(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Удаление расхода",
            description = "Удаление конкретного расхода по id из базы данных"
    )
    @DeleteMapping("/expenses/{id}") // удалить конкретный расход
    public ResponseEntity<Void> deleteExpenseOperation(@PathVariable("id") Long id) {
        boolean deleted = financeFacadeExpense.deleteExpense(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Добавление новой категории в базу",
            description = "Принимает DTO категории и маппит ее до сущности и сохраняет в базе"
    )
    @PostMapping("/addCategory") // добавить категорию
    public ResponseEntity<String> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        categoryService.create(categoryDto);
        return ResponseEntity.status(201).body("Категория успешно добавлена");
    }

    @Operation(
            summary = "Выводит все категории",
            description = "Получает из базы все сохраненные категории"
    )
    @GetMapping("/categories") // получить все категории
    public List<CategoryDto> listCategories() {
        return categoryService.getAll();
    }

    @Operation(
            summary = "Удаляет категорию",
            description = "Принимает id в параметрах, ищет в базе категорию и удаляет ее"
    )
    @DeleteMapping("/category/{id}") // удалить конкретную категорию
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") Long id) {
        boolean deleted = categoryService.delete(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
