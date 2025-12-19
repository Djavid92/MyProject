package com.example.demo.DjavidMustafaev.repositories;

import com.example.demo.DjavidMustafaev.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}
