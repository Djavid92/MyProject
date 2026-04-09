package djavidmustafaev.io.financetracker.service.serviceCategory;

import djavidmustafaev.io.financetracker.dto.CategoryDto;
import djavidmustafaev.io.financetracker.mapper.CategoryMapper;
import djavidmustafaev.io.financetracker.model.Category;
import djavidmustafaev.io.financetracker.repositories.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getAll_shouldReturnMappedDtos() {
        Category category = new Category();
        CategoryDto dto = new CategoryDto();

        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(categoryMapper.toCategoryDto(category)).thenReturn(dto);

        List<CategoryDto> result = categoryService.getAll();

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
        verify(categoryRepository).findAll();
    }

    // 🔹 create()

    @Test
    void create_shouldSaveAndReturnDto() {
        CategoryDto inputDto = new CategoryDto();
        Category entity = new Category();
        Category saved = new Category();
        CategoryDto outputDto = new CategoryDto();

        when(categoryMapper.toCategoryEntity(inputDto)).thenReturn(entity);
        when(categoryRepository.save(entity)).thenReturn(saved);
        when(categoryMapper.toCategoryDto(saved)).thenReturn(outputDto);

        CategoryDto result = categoryService.create(inputDto);

        assertEquals(outputDto, result);
        verify(categoryRepository).save(entity);
    }

    // 🔹 delete() — успешный сценарий

    @Test
    void delete_shouldReturnTrue_ifExists() {
        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(new Category()));

        boolean result = categoryService.delete(1L);

        assertTrue(result);
        verify(categoryRepository).deleteById(1L);
    }

    // 🔹 delete() — если не найдено

    @Test
    void delete_shouldReturnFalse_ifNotExists() {
        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        boolean result = categoryService.delete(1L);

        assertFalse(result);
        verify(categoryRepository, never()).deleteById(any());
    }
}