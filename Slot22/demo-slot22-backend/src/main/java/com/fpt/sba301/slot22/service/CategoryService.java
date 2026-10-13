package com.fpt.sba301.slot22.service;

import com.fpt.sba301.slot22.dto.CategoryRequest;
import com.fpt.sba301.slot22.dto.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    Page<CategoryResponse> findAll(String keyword, Pageable pageable);
    CategoryResponse getById(short id);
    CategoryResponse create(CategoryRequest request);
    CategoryResponse update(short id, CategoryRequest request);
    void delete(short id);
}
