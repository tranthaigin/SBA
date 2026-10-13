package com.fpt.sba301.slot22.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fpt.sba301.slot22.dto.CategoryRequest;
import com.fpt.sba301.slot22.entity.Category;
import com.fpt.sba301.slot22.exception.BusinessConflictException;
import com.fpt.sba301.slot22.exception.ResourceNotFoundException;
import com.fpt.sba301.slot22.repository.CategoryRepository;
import com.fpt.sba301.slot22.repository.NewsArticleRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock CategoryRepository repository;
    @Mock NewsArticleRepository newsArticleRepository;
    @InjectMocks CategoryServiceImpl service;

    @Test
    void createDuplicateName_rejectsBeforeSave() {
        when(repository.existsByCategoryNameIgnoreCase("academic NEWS")).thenReturn(true);
        assertThrows(BusinessConflictException.class, () -> service.create(
                new CategoryRequest(" academic NEWS ", "Description", null, true)));
        verify(repository, never()).save(any(Category.class));
    }

    @Test
    void getMissingId_throwsNotFound() {
        when(repository.findById((short) 99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getById((short) 99));
    }

    @Test
    void deleteReferencedCategory_doesNotDelete() {
        Category category = category();
        when(repository.findById((short) 1)).thenReturn(Optional.of(category));
        when(newsArticleRepository.existsByCategory_CategoryId((short) 1)).thenReturn(true);
        assertThrows(BusinessConflictException.class, () -> service.delete((short) 1));
        verify(repository, never()).delete(any(Category.class));
    }

    @Test
    void updateExisting_keepsIdAndUpdatesAllowedFields() {
        Category category = category();
        when(repository.findById((short) 1)).thenReturn(Optional.of(category));
        when(repository.save(category)).thenReturn(category);
        var response = service.update((short) 1,
                new CategoryRequest(" Updated ", " New description ", (short) 2, false));
        assertEquals((short) 1, response.categoryId());
        assertEquals("Updated", response.categoryName());
        assertEquals("New description", response.categoryDescription());
        assertEquals((short) 2, response.parentCategoryId());
        assertEquals(false, response.active());
        verify(repository).save(category);
    }

    @Test
    void updateMissing_doesNotCreateNewRow() {
        when(repository.findById((short) 99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.update((short) 99,
                new CategoryRequest("Name", "Description", null, true)));
        verify(repository, never()).save(any(Category.class));
    }

    @Test
    void updateDuplicate_keepsOriginalFields() {
        Category category = category();
        when(repository.findById((short) 1)).thenReturn(Optional.of(category));
        when(repository.existsByCategoryNameIgnoreCaseAndCategoryIdNot("Student Affairs", (short) 1))
                .thenReturn(true);
        assertThrows(BusinessConflictException.class, () -> service.update((short) 1,
                new CategoryRequest("Student Affairs", "Changed", null, false)));
        assertEquals("Academic news", category.getCategoryName());
        verify(repository, never()).save(any(Category.class));
    }

    private Category category() {
        Category category = new Category();
        category.setCategoryId((short) 1);
        category.setCategoryName("Academic news");
        category.setCategoryDescription("Academic announcements");
        category.setActive(true);
        return category;
    }
}
