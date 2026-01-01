package com.example.demo.DjavidMustafaev.repositories;
import com.example.demo.DjavidMustafaev.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    @Query("select i from Expense i where i.date >= :startDate and i.date <= :endDate")
    List<Expense> findExpenseByDateRange(@Param("startDate")LocalDate startDate, @Param("endDate")LocalDate endDate);
    // сумма между датами (включительно)
    @Query("select cast(coalesce(sum(e.amount), 0) as java.math.BigDecimal) " +
            "from Expense e where e.date >= :start and e.date <= :end")
    BigDecimal sumAmountBetween(LocalDate start, LocalDate end);

}
