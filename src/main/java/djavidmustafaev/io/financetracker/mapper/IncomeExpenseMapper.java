package djavidmustafaev.io.financetracker.mapper;

import djavidmustafaev.io.financetracker.dto.ExpenseDto;
import djavidmustafaev.io.financetracker.dto.IncomeDto;
import djavidmustafaev.io.financetracker.model.Expense;
import djavidmustafaev.io.financetracker.model.Income;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface IncomeExpenseMapper {

    @Mapping(source = "category", target = "category")
    @Mapping(source = "category.id", target = "categoryId")
    ExpenseDto toExpenseDto(Expense expense);

    Expense toExpenseEntity(ExpenseDto expenseDto);

    @Mapping(source = "category", target = "category")
    @Mapping(source = "category.id", target = "categoryId")
    IncomeDto toIncomeDto (Income income);
    Income toIncomeEntity (IncomeDto incomeDto);
}
