package com.example.demo.DjavidMustafaev.mapper;

import com.example.demo.DjavidMustafaev.dto.CategoryDto;
import com.example.demo.DjavidMustafaev.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toCategoryDto(Category category);
    Category toCategoryEntity(CategoryDto categoryDto);
}
