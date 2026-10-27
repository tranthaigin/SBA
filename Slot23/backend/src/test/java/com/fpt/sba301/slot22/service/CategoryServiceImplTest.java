package com.fpt.sba301.slot22.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import com.fpt.sba301.slot22.dto.CategoryRequest;
import com.fpt.sba301.slot22.entity.Category;
import com.fpt.sba301.slot22.exception.BusinessConflictException;
import com.fpt.sba301.slot22.repository.CategoryRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository repository;

    @InjectMocks
    private CategoryServiceImpl service;

    @Test
    void createTrimsFieldsAndDefaultsActiveToTrue() {
        CategoryRequest request = new CategoryRequest("  Technology  ", "  Software news  ", null, null);
        when(repository.existsByCategoryNameIgnoreCase("Technology")).thenReturn(false);
        when(repository.save(any(Category.class))).thenAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            category.setCategoryId((short) 4);
            return category;
        });

        var response = service.create(request);

        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        verify(repository).save(categoryCaptor.capture());
        Category saved = categoryCaptor.getValue();
        assertEquals("Technology", saved.getCategoryName());
        assertEquals("Software news", saved.getCategoryDescription());
        assertEquals(Boolean.TRUE, saved.getActive());
        assertEquals((short) 4, response.categoryId());
    }

    @Test
    void createRejectsDuplicateName() {
        CategoryRequest request = new CategoryRequest("Technology", "Software news", null, true);
        when(repository.existsByCategoryNameIgnoreCase("Technology")).thenReturn(true);

        assertThrows(BusinessConflictException.class, () -> service.create(request));

        verify(repository, never()).save(any(Category.class));
    }

    @Test
    void deleteTranslatesReferencedCategoryConstraintToConflict() {
        when(repository.findById((short) 1)).thenReturn(java.util.Optional.of(new Category()));
        doThrow(new DataIntegrityViolationException("foreign key"))
                .when(repository).flush();

        BusinessConflictException exception = assertThrows(
                BusinessConflictException.class,
                () -> service.delete((short) 1));

        assertEquals(
                "Cannot delete this category because it is being used by one or more news articles",
                exception.getMessage());
    }
}
