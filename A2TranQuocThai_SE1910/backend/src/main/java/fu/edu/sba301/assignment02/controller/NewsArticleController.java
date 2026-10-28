package fu.edu.sba301.assignment02.controller;

import fu.edu.sba301.assignment02.dto.NewsArticleRequest;
import fu.edu.sba301.assignment02.entity.NewsArticle;
import fu.edu.sba301.assignment02.service.NewsArticleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "*")
public class NewsArticleController {

    private final NewsArticleService newsArticleService;

    public NewsArticleController(NewsArticleService newsArticleService) {
        this.newsArticleService = newsArticleService;
    }

    @GetMapping
    public ResponseEntity<List<NewsArticle>> getAll(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(newsArticleService.getAllNews(search));
    }

    @GetMapping("/active")
    public ResponseEntity<List<NewsArticle>> getActive() {
        return ResponseEntity.ok(newsArticleService.getActiveNews());
    }

    @GetMapping("/created-by/{userId}")
    public ResponseEntity<List<NewsArticle>> getCreatedBy(@PathVariable Short userId, jakarta.servlet.http.HttpServletRequest request) {
        userId=((fu.edu.sba301.assignment02.entity.SystemAccount)request.getAttribute("account")).getAccountId();
        return ResponseEntity.ok(newsArticleService.getNewsCreatedBy(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsArticle> getById(@PathVariable String id) {
        return ResponseEntity.ok(newsArticleService.getNewsById(id));
    }

    @PostMapping
    public ResponseEntity<NewsArticle> create(@Valid @RequestBody NewsArticleRequest request, jakarta.servlet.http.HttpServletRequest http) {
        request.setCreatedById(((fu.edu.sba301.assignment02.entity.SystemAccount)http.getAttribute("account")).getAccountId());
        return ResponseEntity.status(HttpStatus.CREATED).body(newsArticleService.createNews(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NewsArticle> update(@PathVariable String id,
                                              @Valid @RequestBody NewsArticleRequest request,
                                              @RequestParam(required = false) Short updatedById) {
        return ResponseEntity.ok(newsArticleService.updateNews(id, request, updatedById));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        newsArticleService.deleteNews(id);
        return ResponseEntity.noContent().build();
    }
}
