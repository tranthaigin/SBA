package com.fpt.sba301.slot22.service;

import com.fpt.sba301.slot22.dto.NewsArticleRequest;
import com.fpt.sba301.slot22.dto.NewsArticleResponse;
import com.fpt.sba301.slot22.dto.NewsArticleUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NewsArticleService {
    Page<NewsArticleResponse> findAll(String keyword, Pageable pageable);
    NewsArticleResponse getById(String id);
    NewsArticleResponse create(NewsArticleRequest request);
    NewsArticleResponse update(String id, NewsArticleUpdateRequest request);
    void delete(String id);
}
