package fu.edu.sba301.assignment02.controller;

import fu.edu.sba301.assignment02.dto.NewsArticleRequest;
import fu.edu.sba301.assignment02.entity.NewsArticle;
import fu.edu.sba301.assignment02.entity.SystemAccount;
import fu.edu.sba301.assignment02.service.NewsArticleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller cung cấp RESTful API quản lý Bài viết tin tức (NewsArticle).
 * Hỗ trợ xem tin tức công khai, CRUD tin tức kèm Tags và tra cứu lịch sử tác giả.
 */
@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "*")
public class NewsArticleController {

    private final NewsArticleService newsArticleService;

    public NewsArticleController(NewsArticleService newsArticleService) {
        this.newsArticleService = newsArticleService;
    }

    /**
     * Lấy danh sách toàn bộ tin tức cho quản trị hoặc lọc theo từ khóa tìm kiếm.
     */
    @GetMapping
    public ResponseEntity<List<NewsArticle>> getAll(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(newsArticleService.getAllNews(search));
    }

    /**
     * Lấy danh sách bài viết tin tức đang ở trạng thái kích hoạt (Active) phục vụ độc giả công khai.
     */
    @GetMapping("/active")
    public ResponseEntity<List<NewsArticle>> getActive() {
        return ResponseEntity.ok(newsArticleService.getActiveNews());
    }

    /**
     * Lấy danh sách toàn bộ tin tức do chính tài khoản Staff đang đăng nhập tạo ra (lịch sử đăng bài).
     */
    @GetMapping("/created-by/{userId}")
    public ResponseEntity<List<NewsArticle>> getCreatedBy(@PathVariable Short userId, HttpServletRequest request) {
        Short currentUserId = ((SystemAccount) request.getAttribute("account")).getAccountId();
        return ResponseEntity.ok(newsArticleService.getNewsCreatedBy(currentUserId));
    }

    /**
     * Xem thông tin chi tiết một bài viết tin tức theo chuỗi mã ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<NewsArticle> getById(@PathVariable String id) {
        return ResponseEntity.ok(newsArticleService.getNewsById(id));
    }

    /**
     * Tạo mới bài viết tin tức; tự động trích xuất mã tác giả (createdById) từ phiên làm việc session.
     */
    @PostMapping
    public ResponseEntity<NewsArticle> create(@Valid @RequestBody NewsArticleRequest request, HttpServletRequest http) {
        request.setCreatedById(((SystemAccount) http.getAttribute("account")).getAccountId());
        return ResponseEntity.status(HttpStatus.CREATED).body(newsArticleService.createNews(request));
    }

    /**
     * Cập nhật thông tin bài viết tin tức đã tồn tại, bao gồm nội dung, danh mục và danh sách thẻ tags.
     */
    @PutMapping("/{id}")
    public ResponseEntity<NewsArticle> update(@PathVariable String id,
                                              @Valid @RequestBody NewsArticleRequest request,
                                              @RequestParam(required = false) Short updatedById) {
        return ResponseEntity.ok(newsArticleService.updateNews(id, request, updatedById));
    }

    /**
     * Xóa bài viết tin tức theo ID khỏi hệ thống cơ sở dữ liệu.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        newsArticleService.deleteNews(id);
        return ResponseEntity.noContent().build();
    }
}
