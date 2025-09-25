package com.example.demo.DjavidMustafaev.mapper;

import com.example.demo.DjavidMustafaev.dto.ExpenseDto;
import com.example.demo.DjavidMustafaev.dto.IncomeDto;
import com.example.demo.DjavidMustafaev.model.Expense;
import com.example.demo.DjavidMustafaev.model.Income;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface IncomeExpenseMapper {
    ExpenseDto toExpenseDto(Expense expense);
    Expense toExpenseEntity(ExpenseDto expenseDto);

    IncomeDto toIncomeDto (Income income);
    Income toIncomeEntity (IncomeDto incomeDto);
}
