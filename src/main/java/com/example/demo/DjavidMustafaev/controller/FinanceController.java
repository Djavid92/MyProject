package com.example.demo.DjavidMustafaev.controller;

import com.example.demo.DjavidMustafaev.dto.ExpenseDto;
import com.example.demo.DjavidMustafaev.dto.IncomeDto;
import com.example.demo.DjavidMustafaev.service.serviceExpense.FinanceFacadeExpense;
import com.example.demo.DjavidMustafaev.service.serviceIncome.FinanceFacadeIncome;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceFacadeIncome financeFacadeIncome;
    private final FinanceFacadeExpense financeFacadeExpense;


    @GetMapping("/totals")
    public ResponseEntity<?> getTotalIncomeAndExpense() {
        BigDecimal totalIncome = financeFacadeIncome.totalIncomeForCurrentMonth();
        BigDecimal totalExpense = financeFacadeExpense.totalExpenseForCurrentMonth();
        return ResponseEntity.ok(new Object() {
            public final BigDecimal Income = totalIncome;
            public final BigDecimal Expense = totalExpense;
        });
    }

    @GetMapping("/incomes")
    public List<IncomeDto> getIncomes() {
        return financeFacadeIncome.listIncome();
    }

    @GetMapping("/expenses")
    public List<ExpenseDto> getExpense() {
        return financeFacadeExpense.listExpenses();
    }

    @PostMapping("/addIncome")
    public ResponseEntity<String> addIncome(@Valid @RequestBody IncomeDto incomeDto) {
        financeFacadeIncome.addIncome(incomeDto);
        return ResponseEntity.status(201).body("Доход успешно добавлен");
    }

    @PostMapping("/addExpense")
    public ResponseEntity<String> addExpense(@Valid @RequestBody ExpenseDto expenseDto) {
        financeFacadeExpense.addExpense(expenseDto);
        return ResponseEntity.status(201).body("Расход успешно добавлен");
    }

    @DeleteMapping("/delete/all")
    public ResponseEntity<String> deleteAll() {
        financeFacadeIncome.deleteAll();
        financeFacadeExpense.deleteAll();
        return ResponseEntity.ok("Все успешно удалилось");
    }

    @DeleteMapping("/incomes/{id}")
    public ResponseEntity<Void> deleteIncomeOperation(@PathVariable ("id") Long id) {
        boolean deleted = financeFacadeIncome.deleteIncome(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<Void> deleteExpenseOperation(@PathVariable ("id") Long id) {
        boolean deleted = financeFacadeExpense.deleteExpense(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

//    @GetMapping("/test/exception")
//    public ResponseEntity<String> testException() {
//        financeService.testException();
//        return ResponseEntity.ok("Тест завершен");
//    }
//
//    @GetMapping("/test/nullpointer")
//    public ResponseEntity<String> testNullPointer() {
//        financeService.testNullPointer();
//        return ResponseEntity.ok("Тест завершен");
//    }
}
