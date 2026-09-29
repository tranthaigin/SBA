package com.fpt.sba301.slot13.controller;

import com.fpt.sba301.slot13.model.News;
import com.fpt.sba301.slot13.service.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public List<News> getAll(
            @RequestParam(required = false) String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return newsService.findAll();
        }
        return newsService.search(keyword);
    }

    @GetMapping("/{id}")
    public News getOne(@PathVariable Long id) {
        return newsService.findById(id);
    }

    @PostMapping
    public ResponseEntity<News> create(@RequestBody News request) {
        News created = newsService.create(request);
        URI location = URI.create("/api/news/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<News> update(
            @PathVariable Long id,
            @RequestBody News request) {
        return ResponseEntity.ok(newsService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        newsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
