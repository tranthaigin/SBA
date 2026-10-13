package com.fpt.sba301.slot22.service;

import com.fpt.sba301.slot22.dto.CategoryRequest;
import com.fpt.sba301.slot22.dto.CategoryResponse;
import com.fpt.sba301.slot22.entity.Category;
import com.fpt.sba301.slot22.exception.BusinessConflictException;
import com.fpt.sba301.slot22.exception.ResourceNotFoundException;
import com.fpt.sba301.slot22.repository.CategoryRepository;
import com.fpt.sba301.slot22.repository.NewsArticleRepository;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private final NewsArticleRepository newsArticleRepository;

    public CategoryServiceImpl(CategoryRepository repository, NewsArticleRepository newsArticleRepository) {
        this.repository = repository;
        this.newsArticleRepository = newsArticleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> findAll(String keyword, Pageable pageable) {
        Set<String> allowedSort = Set.of("categoryId", "categoryName", "categoryDescription", "parentCategoryId", "active");
        if (pageable.getSort().stream().anyMatch(order -> !allowedSort.contains(order.getProperty()))) {
            throw new IllegalArgumentException("Unsupported category sort field");
        }
        Page<Category> page = keyword == null || keyword.isBlank()
                ? repository.findAll(pageable)
                : repository.findByCategoryNameContainingIgnoreCase(keyword.trim(), pageable);
        return page.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(short id) {
        return toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (repository.existsByCategoryNameIgnoreCase(request.categoryName().trim())) {
            throw new BusinessConflictException("Category name already exists");
        }
        Category category = new Category();
        apply(category, request);
        return toResponse(repository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse update(short id, CategoryRequest request) {
        Category category = findEntity(id);
        if (repository.existsByCategoryNameIgnoreCaseAndCategoryIdNot(request.categoryName().trim(), id)) {
            throw new BusinessConflictException("Category name already exists");
        }
        apply(category, request);
        return toResponse(repository.save(category));
    }

    @Override
    @Transactional
    public void delete(short id) {
        Category category = findEntity(id);
        if (newsArticleRepository.existsByCategory_CategoryId(id)) {
            throw new BusinessConflictException("Category is used by one or more news articles");
        }
        repository.delete(category);
    }

    private Category findEntity(short id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category " + id + " not found"));
    }

    private void apply(Category category, CategoryRequest request) {
        category.setCategoryName(request.categoryName().trim());
        category.setCategoryDescription(request.categoryDescription().trim());
        category.setParentCategoryId(request.parentCategoryId());
        category.setActive(request.active() == null ? Boolean.TRUE : request.active());
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getCategoryId(), category.getCategoryName(),
                category.getCategoryDescription(), category.getParentCategoryId(), category.getActive());
    }
}
