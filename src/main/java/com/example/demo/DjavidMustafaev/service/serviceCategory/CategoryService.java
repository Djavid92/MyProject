package com.example.demo.DjavidMustafaev.service.serviceCategory;

import com.example.demo.DjavidMustafaev.dto.CategoryDto;
import com.example.demo.DjavidMustafaev.mapper.CategoryMapper;
import com.example.demo.DjavidMustafaev.model.Category;
import com.example.demo.DjavidMustafaev.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDto> getAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toCategoryDto).toList();
    }

    public void create(CategoryDto categoryDto) { // проверить этот метод на null
        categoryRepository.save(categoryMapper.toCategoryEntity(categoryDto));
    }

    public boolean delete(Long id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        categoryOptional.ifPresent(category -> categoryRepository.deleteById(id));
        return categoryOptional.isPresent();
    }
}
