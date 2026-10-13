package com.fpt.sba301.slot22.repository;

import com.fpt.sba301.slot22.entity.NewsArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsArticleRepository extends JpaRepository<NewsArticle, String> {
    boolean existsByCategory_CategoryId(Short categoryId);
    Page<NewsArticle> findByNewsArticleIdContainingIgnoreCase(String keyword, Pageable pageable);
}
