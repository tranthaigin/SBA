package com.fpt.sba301.slot22.controller;

import com.fpt.sba301.slot22.dto.NewsArticleRequest;
import com.fpt.sba301.slot22.dto.NewsArticleResponse;
import com.fpt.sba301.slot22.dto.NewsArticleUpdateRequest;
import com.fpt.sba301.slot22.service.NewsArticleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/news-articles")
public class NewsArticleController {
    private final NewsArticleService service;

    public NewsArticleController(NewsArticleService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<NewsArticleResponse>> getAll(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "newsArticleId") Pageable pageable) {
        return ResponseEntity.ok(service.findAll(keyword, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsArticleResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<NewsArticleResponse> create(@Valid @RequestBody NewsArticleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NewsArticleResponse> update(@PathVariable String id,
            @Valid @RequestBody NewsArticleUpdateRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
