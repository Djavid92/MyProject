package com.example.demo.DjavidMustafaev.repositories;

import com.example.demo.DjavidMustafaev.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {

    // сумма между датами (включительно)
    @Query("select coalesce(sum(i.amount), 0) from Income i where i.date >= :start and i.date <= :end")
    BigDecimal sumAmountBetween(LocalDate start, LocalDate end);

}
