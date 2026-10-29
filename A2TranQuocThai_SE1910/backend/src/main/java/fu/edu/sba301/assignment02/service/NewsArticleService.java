package fu.edu.sba301.assignment02.service;

import fu.edu.sba301.assignment02.dto.NewsArticleRequest;
import fu.edu.sba301.assignment02.entity.Category;
import fu.edu.sba301.assignment02.entity.NewsArticle;
import fu.edu.sba301.assignment02.entity.SystemAccount;
import fu.edu.sba301.assignment02.entity.Tag;
import fu.edu.sba301.assignment02.exception.ResourceNotFoundException;
import fu.edu.sba301.assignment02.repository.CategoryRepository;
import fu.edu.sba301.assignment02.repository.NewsArticleRepository;
import fu.edu.sba301.assignment02.repository.SystemAccountRepository;
import fu.edu.sba301.assignment02.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service xử lý logic nghiệp vụ cho bài viết tin tức (NewsArticle).
 * Quản lý liên kết Category, SystemAccount (tác giả) và bảng liên kết Many-to-Many Tags.
 */
@Service
@Transactional
public class NewsArticleService {

    private final NewsArticleRepository newsArticleRepository;
    private final CategoryRepository categoryRepository;
    private final SystemAccountRepository accountRepository;
    private final TagRepository tagRepository;

    public NewsArticleService(NewsArticleRepository newsArticleRepository,
                              CategoryRepository categoryRepository,
                              SystemAccountRepository accountRepository,
                              TagRepository tagRepository) {
        this.newsArticleRepository = newsArticleRepository;
        this.categoryRepository = categoryRepository;
        this.accountRepository = accountRepository;
        this.tagRepository = tagRepository;
    }

    /**
     * Lấy toàn bộ danh sách tin tức hoặc lọc theo tiêu đề / nội dung.
     */
    public List<NewsArticle> getAllNews(String search) {
        if (search != null && !search.isBlank()) {
            return newsArticleRepository.findByNewsTitleContainingIgnoreCaseOrHeadlineContainingIgnoreCase(search.trim(), search.trim());
        }
        return newsArticleRepository.findAll();
    }

    /**
     * Lấy danh sách các bài viết tin tức đang ở trạng thái kích hoạt (newsStatus = true).
     */
    public List<NewsArticle> getActiveNews() {
        return newsArticleRepository.findByNewsStatusTrue();
    }

    /**
     * Lấy danh sách tin tức do một tài khoản người dùng cụ thể đã tạo.
     */
    public List<NewsArticle> getNewsCreatedBy(Short userId) {
        return newsArticleRepository.findByCreatedById(userId);
    }

    /**
     * Lấy chi tiết bài viết tin tức theo chuỗi ID.
     */
    public NewsArticle getNewsById(String id) {
        return newsArticleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("NewsArticle not found with ID: " + id));
    }

    /**
     * Tạo mới bài viết tin tức: tự động gán ngày tạo, liên kết category, tác giả và danh sách tags.
     */
    public NewsArticle createNews(NewsArticleRequest req) {
        Category cat = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + req.getCategoryId()));

        SystemAccount creator = accountRepository.findById(req.getCreatedById())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + req.getCreatedById()));

        NewsArticle article = new NewsArticle();
        article.setNewsArticleId(req.getNewsArticleId() != null && !req.getNewsArticleId().isBlank()
                ? req.getNewsArticleId().trim()
                : java.util.UUID.randomUUID().toString().substring(0, 8));
        article.setNewsTitle(req.getNewsTitle().trim());
        article.setHeadline(req.getHeadline() != null ? req.getHeadline().trim() : "");
        article.setNewsContent(req.getNewsContent() != null ? req.getNewsContent().trim() : "");
        article.setNewsSource(req.getNewsSource() != null ? req.getNewsSource().trim() : "");
        article.setCategory(cat);
        article.setNewsStatus(req.getNewsStatus() != null ? req.getNewsStatus() : true);
        article.setCreatedBy(creator);
        article.setCreatedDate(LocalDateTime.now());
        article.setModifiedDate(LocalDateTime.now());

        if (req.getTagIds() != null && !req.getTagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(req.getTagIds()));
            article.setTags(tags);
        }

        return newsArticleRepository.save(article);
    }

    /**
     * Cập nhật thông tin bài viết tin tức và tự động cập nhật ngày chỉnh sửa modifiedDate.
     */
    public NewsArticle updateNews(String id, NewsArticleRequest req, Short updatedById) {
        NewsArticle article = getNewsById(id);

        if (req.getCategoryId() != null) {
            Category cat = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + req.getCategoryId()));
            article.setCategory(cat);
        }

        if (req.getNewsTitle() != null) article.setNewsTitle(req.getNewsTitle().trim());
        if (req.getHeadline() != null) article.setHeadline(req.getHeadline().trim());
        if (req.getNewsContent() != null) article.setNewsContent(req.getNewsContent().trim());
        if (req.getNewsSource() != null) article.setNewsSource(req.getNewsSource().trim());
        if (req.getNewsStatus() != null) article.setNewsStatus(req.getNewsStatus());

        if (updatedById != null) {
            SystemAccount updater = accountRepository.findById(updatedById).orElse(null);
            article.setUpdatedBy(updater);
        }

        article.setModifiedDate(LocalDateTime.now());

        if (req.getTagIds() != null) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(req.getTagIds()));
            article.setTags(tags);
        }

        return newsArticleRepository.save(article);
    }

    /**
     * Xóa bài viết tin tức khỏi cơ sở dữ liệu theo ID.
     */
    public void deleteNews(String id) {
        NewsArticle article = getNewsById(id);
        newsArticleRepository.delete(article);
    }
}
