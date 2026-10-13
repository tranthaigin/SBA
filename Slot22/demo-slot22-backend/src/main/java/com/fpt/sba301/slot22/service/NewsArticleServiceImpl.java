package com.fpt.sba301.slot22.service;

import com.fpt.sba301.slot22.dto.NewsArticleRequest;
import com.fpt.sba301.slot22.dto.NewsArticleResponse;
import com.fpt.sba301.slot22.dto.NewsArticleUpdateRequest;
import com.fpt.sba301.slot22.entity.Category;
import com.fpt.sba301.slot22.entity.NewsArticle;
import com.fpt.sba301.slot22.exception.BusinessConflictException;
import com.fpt.sba301.slot22.exception.ResourceNotFoundException;
import com.fpt.sba301.slot22.repository.CategoryRepository;
import com.fpt.sba301.slot22.repository.NewsArticleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NewsArticleServiceImpl implements NewsArticleService {
    private final NewsArticleRepository repository;
    private final CategoryRepository categoryRepository;

    public NewsArticleServiceImpl(NewsArticleRepository repository, CategoryRepository categoryRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NewsArticleResponse> findAll(String keyword, Pageable pageable) {
        if (pageable.getSort().stream().anyMatch(order -> !order.getProperty().equals("newsArticleId"))) {
            throw new IllegalArgumentException("Unsupported news article sort field");
        }
        Page<NewsArticle> page = keyword == null || keyword.isBlank()
                ? repository.findAll(pageable)
                : repository.findByNewsArticleIdContainingIgnoreCase(keyword.trim(), pageable);
        return page.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public NewsArticleResponse getById(String id) {
        return toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public NewsArticleResponse create(NewsArticleRequest request) {
        String id = request.newsArticleId().trim();
        if (repository.existsById(id)) {
            throw new BusinessConflictException("News article ID already exists");
        }
        Category category = findCategory(request.categoryId());
        NewsArticle article = new NewsArticle();
        article.setNewsArticleId(id);
        article.setCategory(category);
        return toResponse(repository.save(article));
    }

    @Override
    @Transactional
    public NewsArticleResponse update(String id, NewsArticleUpdateRequest request) {
        NewsArticle article = findEntity(id);
        article.setCategory(findCategory(request.categoryId()));
        return toResponse(repository.save(article));
    }

    @Override
    @Transactional
    public void delete(String id) {
        repository.delete(findEntity(id));
    }

    private NewsArticle findEntity(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News article " + id + " not found"));
    }

    private Category findCategory(short id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category " + id + " not found"));
    }

    private NewsArticleResponse toResponse(NewsArticle article) {
        return new NewsArticleResponse(article.getNewsArticleId(), article.getCategory().getCategoryId());
    }
}
