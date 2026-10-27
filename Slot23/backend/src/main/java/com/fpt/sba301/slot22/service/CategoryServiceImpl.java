package com.fpt.sba301.slot22.service;

import com.fpt.sba301.slot22.dto.CategoryRequest;
import com.fpt.sba301.slot22.dto.CategoryResponse;
import com.fpt.sba301.slot22.entity.Category;
import com.fpt.sba301.slot22.exception.BusinessConflictException;
import com.fpt.sba301.slot22.exception.ResourceNotFoundException;
import com.fpt.sba301.slot22.repository.CategoryRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    public CategoryServiceImpl(CategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> findAll(String keyword, Pageable pageable) {
        Page<Category> categories = keyword == null || keyword.isBlank()
                ? repository.findAll(pageable)
                : repository.findByCategoryNameContainingIgnoreCase(keyword.trim(), pageable);
        return categories.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(short id) {
        return toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        String categoryName = request.categoryName().trim();
        if (repository.existsByCategoryNameIgnoreCase(categoryName)) {
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
        String categoryName = request.categoryName().trim();
        if (repository.existsByCategoryNameIgnoreCaseAndCategoryIdNot(categoryName, id)) {
            throw new BusinessConflictException("Category name already exists");
        }

        apply(category, request);
        return toResponse(repository.save(category));
    }

    @Override
    @Transactional
    public void delete(short id) {
        try {
            repository.delete(findEntity(id));
            repository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessConflictException(
                    "Cannot delete this category because it is being used by one or more news articles");
        }
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
        return new CategoryResponse(
                category.getCategoryId(),
                category.getCategoryName(),
                category.getCategoryDescription(),
                category.getParentCategoryId(),
                category.getActive()
        );
    }
}
