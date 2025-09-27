package com.example.demo.DjavidMustafaev.controller;

import com.example.demo.DjavidMustafaev.dto.ExpenseDto;
import com.example.demo.DjavidMustafaev.dto.IncomeDto;
import com.example.demo.DjavidMustafaev.service.FinanceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
public class FinanceController {

    final private FinanceService financeService;

    @Autowired
    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @GetMapping("/totals")
    public ResponseEntity<?> getTotalIncomeAndExpense() {
        BigDecimal totalIncome = financeService.calculateTotalIncome();
        BigDecimal totalExpense = financeService.calculateTotalExpenses();
        return ResponseEntity.ok(new Object() {
            public final BigDecimal Income = totalIncome;
            public final BigDecimal Expense = totalExpense;
        });
    }

    @GetMapping("/incomes")
    public List<IncomeDto> getIncomes() {
        return financeService.getAllIncome();
    }

    @GetMapping("/expenses")
    public List<ExpenseDto> getExpense() {
        return financeService.getAllExpense();
    }

    @PostMapping("/addIncome")
    public ResponseEntity<String> addIncome(@Valid @RequestBody IncomeDto incomeDto) {
        financeService.saveIncome(incomeDto);
        return ResponseEntity.status(201).body("Доход успешно добавлен");
    }

    @PostMapping("/addExpense")
    public ResponseEntity<String> addExpense(@Valid @RequestBody ExpenseDto expenseDto) {
        financeService.saveExpense(expenseDto);
        return ResponseEntity.status(201).body("Расход успешно добавлен");
    }

    @DeleteMapping("/delete/all")
    public ResponseEntity<String> deleteAll() {
        financeService.deleteAll();
        return ResponseEntity.ok("Все успешно удалилось");
    }

    @DeleteMapping("/incomes/{id}")
    public ResponseEntity<HttpStatus> deleteIncomeOperation(@PathVariable ("id") Long id) {
        financeService.deleteIncomeOperation(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<HttpStatus> deleteExpenseOperation(@PathVariable ("id") Long id) {
        financeService.deleteExpenseOperation(id);
        return ResponseEntity.ok(HttpStatus.OK);
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
