package com.example.demo.DjavidMustafaev.repositories;

import com.example.demo.DjavidMustafaev.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    @Query("select coalesce(sum(i.amount), 0) from Income i")
    BigDecimal sumAmount();
}
