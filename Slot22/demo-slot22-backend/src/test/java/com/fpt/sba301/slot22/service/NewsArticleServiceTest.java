package com.fpt.sba301.slot22.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fpt.sba301.slot22.dto.NewsArticleRequest;
import com.fpt.sba301.slot22.dto.NewsArticleUpdateRequest;
import com.fpt.sba301.slot22.entity.Category;
import com.fpt.sba301.slot22.entity.NewsArticle;
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
class NewsArticleServiceTest {
    @Mock NewsArticleRepository repository;
    @Mock CategoryRepository categoryRepository;
    @InjectMocks NewsArticleServiceImpl service;

    @Test
    void createMissingCategory_doesNotSaveArticle() {
        when(categoryRepository.findById((short) 99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.create(new NewsArticleRequest("N-NEW", (short) 99)));
        verify(repository, never()).save(any(NewsArticle.class));
    }

    @Test
    void createDuplicateId_doesNotOverwriteArticle() {
        when(repository.existsById("N-EXISTING")).thenReturn(true);
        assertThrows(BusinessConflictException.class,
                () -> service.create(new NewsArticleRequest("N-EXISTING", (short) 1)));
        verify(repository, never()).save(any(NewsArticle.class));
    }

    @Test
    void updateCategory_keepsArticleId() {
        Category category = new Category();
        category.setCategoryId((short) 2);
        NewsArticle article = new NewsArticle();
        article.setNewsArticleId("N-EXISTING");
        when(repository.findById("N-EXISTING")).thenReturn(Optional.of(article));
        when(categoryRepository.findById((short) 2)).thenReturn(Optional.of(category));
        when(repository.save(article)).thenReturn(article);
        var response = service.update("N-EXISTING", new NewsArticleUpdateRequest((short) 2));
        assertEquals("N-EXISTING", response.newsArticleId());
        assertEquals((short) 2, response.categoryId());
    }

    @Test
    void updateMissingCategory_keepsExistingReference() {
        Category original = new Category();
        original.setCategoryId((short) 1);
        NewsArticle article = new NewsArticle();
        article.setNewsArticleId("N-EXISTING");
        article.setCategory(original);
        when(repository.findById("N-EXISTING")).thenReturn(Optional.of(article));
        when(categoryRepository.findById((short) 99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.update("N-EXISTING", new NewsArticleUpdateRequest((short) 99)));
        assertEquals((short) 1, article.getCategory().getCategoryId());
        verify(repository, never()).save(any(NewsArticle.class));
    }
}
