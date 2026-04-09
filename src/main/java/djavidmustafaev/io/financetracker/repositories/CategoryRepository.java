package djavidmustafaev.io.financetracker.repositories;

import djavidmustafaev.io.financetracker.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}
