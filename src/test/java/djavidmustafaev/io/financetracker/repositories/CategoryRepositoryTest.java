package djavidmustafaev.io.financetracker.repositories;

import djavidmustafaev.io.financetracker.model.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private Category buildCategory(String name) {
        Category c = new Category();
        c.setName(name);
        return c;
    }

    @Test
    void save_shouldPersistAndAssignId() {
        Category saved = categoryRepository.save(buildCategory("Food"));
        assertNotNull(saved.getId());
        assertEquals("Food", saved.getName());
    }

    @Test
    void findById_shouldReturnCategory_whenExists() {
        Category saved = categoryRepository.save(buildCategory("Travel"));
        Optional<Category> found = categoryRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Travel", found.get().getName());
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        Optional<Category> result = categoryRepository.findById(999L);
        assertFalse(result.isPresent());
    }

    @Test
    void findByName_shouldReturnCategory_whenExists() {
        categoryRepository.save(buildCategory("Health"));
        Optional<Category> result = categoryRepository.findByName("Health");
        assertTrue(result.isPresent());
        assertEquals("Health", result.get().getName());
    }

    @Test
    void findByName_shouldReturnEmpty_whenNotExists() {
        Optional<Category> result = categoryRepository.findByName("NonExistent");
        assertFalse(result.isPresent());
    }

    @Test
    void findAll_shouldReturnAllSavedCategories() {
        categoryRepository.save(buildCategory("Salary"));
        categoryRepository.save(buildCategory("Bonus"));

        List<Category> all = categoryRepository.findAll();
        assertTrue(all.size() >= 2);
        assertTrue(all.stream().anyMatch(c -> c.getName().equals("Salary")));
        assertTrue(all.stream().anyMatch(c -> c.getName().equals("Bonus")));
    }

    @Test
    void delete_shouldRemoveCategory() {
        Category saved = categoryRepository.save(buildCategory("DeleteMe"));
        Long id = saved.getId();

        categoryRepository.deleteById(id);

        assertFalse(categoryRepository.findById(id).isPresent());
    }

    @Test
    void save_shouldUpdateName_whenEntityAlreadyExists() {
        Category saved = categoryRepository.save(buildCategory("OldName"));
        saved.setName("NewName");
        categoryRepository.save(saved);

        Category updated = categoryRepository.findById(saved.getId()).orElseThrow();
        assertEquals("NewName", updated.getName());
    }
}
