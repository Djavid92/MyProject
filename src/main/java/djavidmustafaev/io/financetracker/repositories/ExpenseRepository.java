package djavidmustafaev.io.financetracker.repositories;
import djavidmustafaev.io.financetracker.model.Category;
import djavidmustafaev.io.financetracker.model.Expense;
import djavidmustafaev.io.financetracker.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    @Query("select e from Expense e join fetch e.category where e.date >= :startDate and e.date <= :endDate")
    List<Expense> findExpenseByDateRange(@Param("startDate")LocalDate startDate, @Param("endDate")LocalDate endDate);
    // сумма между датами (включительно)
    @Query("select cast(coalesce(sum(e.amount), 0) as java.math.BigDecimal) " +
            "from Expense e where e.date >= :start and e.date <= :end")
    BigDecimal sumAmountBetween(LocalDate start, LocalDate end);

    // фильтрация по категориям
    @Query("select e from Expense e join fetch e.category where e.category = :category")
    List<Expense> findIncomeByCategory(@Param("category") Category category);

}
