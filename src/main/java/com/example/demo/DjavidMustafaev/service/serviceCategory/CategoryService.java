package com.example.demo.DjavidMustafaev.service.serviceCategory;

import com.example.demo.DjavidMustafaev.dto.CategoryDto;
import com.example.demo.DjavidMustafaev.mapper.CategoryMapper;
import com.example.demo.DjavidMustafaev.model.Category;
import com.example.demo.DjavidMustafaev.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    public static final String CACHEABLE_CATEGORY_VALUE = "categories";
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Cacheable(value = CACHEABLE_CATEGORY_VALUE)
    public List<CategoryDto> getAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toCategoryDto).toList();
    }


    @CacheEvict(value = CACHEABLE_CATEGORY_VALUE, allEntries = true)
    public CategoryDto create(CategoryDto categoryDto) {
        Category category = categoryRepository.save(categoryMapper.toCategoryEntity(categoryDto));
        return categoryMapper.toCategoryDto(category);
    }

    @CacheEvict(value = CACHEABLE_CATEGORY_VALUE, allEntries = true)
    public boolean delete(Long id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        categoryOptional.ifPresent(category -> categoryRepository.deleteById(id));
        return categoryOptional.isPresent();
    }
}
