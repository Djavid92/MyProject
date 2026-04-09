package djavidmustafaev.io.financetracker.mapper;

import djavidmustafaev.io.financetracker.dto.CategoryDto;
import djavidmustafaev.io.financetracker.model.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CategoryMapperImpl.class)
class CategoryMapperTest {

    @Autowired
    private CategoryMapper categoryMapper;

    @Test
    void toCategoryDto_shouldMapAllFields() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Food");

        CategoryDto dto = categoryMapper.toCategoryDto(category);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Food", dto.getName());
    }

    @Test
    void toCategoryDto_shouldReturnNull_whenInputIsNull() {
        assertNull(categoryMapper.toCategoryDto(null));
    }

    @Test
    void toCategoryDto_shouldHandleNullName() {
        Category category = new Category();
        category.setId(2L);
        category.setName(null);

        CategoryDto dto = categoryMapper.toCategoryDto(category);

        assertEquals(2L, dto.getId());
        assertNull(dto.getName());
    }

    @Test
    void toCategoryEntity_shouldMapAllFields() {
        CategoryDto dto = CategoryDto.builder().id(5L).name("Transport").build();

        Category category = categoryMapper.toCategoryEntity(dto);

        assertNotNull(category);
        assertEquals(5L, category.getId());
        assertEquals("Transport", category.getName());
    }

    @Test
    void toCategoryEntity_shouldReturnNull_whenInputIsNull() {
        assertNull(categoryMapper.toCategoryEntity(null));
    }

    @Test
    void roundTrip_entityToDtoAndBack_shouldPreserveData() {
        Category original = new Category();
        original.setId(10L);
        original.setName("Health");

        CategoryDto dto = categoryMapper.toCategoryDto(original);
        Category restored = categoryMapper.toCategoryEntity(dto);

        assertEquals(original.getId(), restored.getId());
        assertEquals(original.getName(), restored.getName());
    }
}
