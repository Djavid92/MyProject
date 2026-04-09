package djavidmustafaev.io.financetracker.service.serviceCategory;

import djavidmustafaev.io.financetracker.dto.CategoryDto;
import djavidmustafaev.io.financetracker.mapper.CategoryMapper;
import djavidmustafaev.io.financetracker.mapper.IncomeExpenseMapper;
import djavidmustafaev.io.financetracker.model.Category;
import djavidmustafaev.io.financetracker.repositories.CategoryRepository;
import djavidmustafaev.io.financetracker.repositories.ExpenseRepository;
import djavidmustafaev.io.financetracker.repositories.IncomeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class CategoryServiceCacheTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CacheManager cacheManager;

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private CategoryMapper categoryMapper;

    @MockBean
    private ExpenseRepository expenseRepository;

    @MockBean
    private IncomeRepository incomeRepository;

    @MockBean
    private IncomeExpenseMapper incomeExpenseMapper;

    @BeforeEach
    void clearCaches() {
        cacheManager.getCacheNames().forEach(name ->
                Objects.requireNonNull(cacheManager.getCache(name)).clear());
    }

    @Test
    void getAll_shouldCacheResult_andCallRepositoryOnlyOnce() {
        Category cat = new Category();
        cat.setId(1L);
        cat.setName("Food");
        when(categoryRepository.findAll()).thenReturn(List.of(cat));
        when(categoryMapper.toCategoryDto(cat)).thenReturn(
                CategoryDto.builder().id(1L).name("Food").build());

        List<CategoryDto> first = categoryService.getAll();
        List<CategoryDto> second = categoryService.getAll();
        List<CategoryDto> third = categoryService.getAll();

        assertEquals(first, second);
        assertEquals(second, third);
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void create_shouldEvictCache_soSubsequentGetAllHitsRepository() {
        Category cat = new Category();
        cat.setId(1L);
        cat.setName("Food");
        when(categoryRepository.findAll()).thenReturn(List.of(cat));
        when(categoryMapper.toCategoryDto(cat)).thenReturn(
                CategoryDto.builder().id(1L).name("Food").build());
        when(categoryRepository.save(any())).thenReturn(cat);
        when(categoryMapper.toCategoryEntity(any())).thenReturn(cat);

        categoryService.getAll();
        verify(categoryRepository, times(1)).findAll();

        categoryService.create(CategoryDto.builder().name("Food").build());

        categoryService.getAll();
        verify(categoryRepository, times(2)).findAll();
    }

    @Test
    void delete_shouldEvictCache_soSubsequentGetAllHitsRepository() {
        Category cat = new Category();
        cat.setId(1L);
        cat.setName("Food");
        when(categoryRepository.findAll()).thenReturn(List.of(cat));
        when(categoryMapper.toCategoryDto(cat)).thenReturn(
                CategoryDto.builder().id(1L).name("Food").build());
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));

        categoryService.getAll();
        verify(categoryRepository, times(1)).findAll();

        categoryService.delete(1L);

        categoryService.getAll();
        verify(categoryRepository, times(2)).findAll();
    }

    @Test
    void delete_nonExistentId_shouldEvictCacheAndReturnFalse() {
        when(categoryRepository.findAll()).thenReturn(List.of());
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        categoryService.getAll();
        boolean result = categoryService.delete(999L);

        assertFalse(result);
        categoryService.getAll();
        verify(categoryRepository, times(2)).findAll();
    }
}
