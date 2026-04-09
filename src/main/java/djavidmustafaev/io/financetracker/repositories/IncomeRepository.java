package djavidmustafaev.io.financetracker.repositories;

import djavidmustafaev.io.financetracker.model.Category;
import djavidmustafaev.io.financetracker.model.Income;
import io.micrometer.core.annotation.Timed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    @Query("select i from Income i join fetch i.category where i.date >= :startDate and i.date <= :endDate")
    List<Income> findIncomesByDateRange(@Param("startDate")LocalDate startDate, @Param("endDate")LocalDate endDate);
    // сумма между датами (включительно)
    @Query("select cast(coalesce(sum(i.amount), 0) as java.math.BigDecimal) " +
            "from Income i where i.date >= :start and i.date <= :end")
    BigDecimal sumAmountBetween(LocalDate start, LocalDate end);
    // фильтрация по категориям
    @Query("select i from Income i join fetch i.category where i.category = :category")
    List<Income> findIncomeByCategory(@Param("category") Category category);

}
