package com.example.demo.DjavidMustafaev.repositories;

import com.example.demo.DjavidMustafaev.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    @Query("select coalesce(sum(e.amount), 0) from Expense e")
    BigDecimal sumAmount();

    // сумма между датами (включительно)
    @Query("select coalesce(sum(e.amount), 0) from Expense e where e.date >= :start and e.date <= :end")
    BigDecimal sumAmountBetween(LocalDate start, LocalDate end);
}
