package fu.edu.sba301.assignment02.service;

import fu.edu.sba301.assignment02.dto.NewsArticleRequest;
import fu.edu.sba301.assignment02.entity.Category;
import fu.edu.sba301.assignment02.entity.NewsArticle;
import fu.edu.sba301.assignment02.entity.Tag;
import fu.edu.sba301.assignment02.exception.ResourceNotFoundException;
import fu.edu.sba301.assignment02.repository.CategoryRepository;
import fu.edu.sba301.assignment02.repository.NewsArticleRepository;
import fu.edu.sba301.assignment02.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class NewsArticleService {

    private final NewsArticleRepository newsArticleRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public NewsArticleService(NewsArticleRepository newsArticleRepository,
                              CategoryRepository categoryRepository,
                              TagRepository tagRepository) {
        this.newsArticleRepository = newsArticleRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
    }

    public List<NewsArticle> getAllNews(String search) {
        if (search != null && !search.isBlank()) {
            return newsArticleRepository.findByNewsTitleContainingIgnoreCaseOrHeadlineContainingIgnoreCase(search.trim(), search.trim());
        }
        return newsArticleRepository.findAll();
    }

    public List<NewsArticle> getActiveNews() {
        return newsArticleRepository.findByNewsStatusTrue();
    }

    public List<NewsArticle> getNewsCreatedBy(Short createdById) {
        return newsArticleRepository.findByCreatedById(createdById);
    }

    public NewsArticle getNewsById(String id) {
        return newsArticleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News Article not found with ID: " + id));
    }

    public NewsArticle createNews(NewsArticleRequest req) {
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + req.getCategoryId()));

        String id = req.getNewsArticleId();
        if (id == null || id.isBlank()) {
            id = "NA" + (System.currentTimeMillis() % 100000000);
        }

        NewsArticle article = new NewsArticle();
        article.setNewsArticleId(id);
        article.setNewsTitle(req.getNewsTitle());
        article.setHeadline(req.getHeadline().trim());
        article.setNewsContent(req.getNewsContent());
        article.setNewsSource(req.getNewsSource());
        article.setCategory(category);
        article.setNewsStatus(req.getNewsStatus() != null ? req.getNewsStatus() : true);
        article.setCreatedById(req.getCreatedById());
        article.setCreatedDate(LocalDateTime.now());
        article.setModifiedDate(LocalDateTime.now());

        if (req.getTagIds() != null && !req.getTagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(req.getTagIds()));
            article.setTags(tags);
        }

        return newsArticleRepository.save(article);
    }

    public NewsArticle updateNews(String id, NewsArticleRequest req, Short updatedById) {
        NewsArticle article = getNewsById(id);

        if (req.getCategoryId() != null) {
            Category category = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + req.getCategoryId()));
            article.setCategory(category);
        }

        if (req.getNewsTitle() != null) article.setNewsTitle(req.getNewsTitle());
        if (req.getHeadline() != null) article.setHeadline(req.getHeadline().trim());
        if (req.getNewsContent() != null) article.setNewsContent(req.getNewsContent());
        if (req.getNewsSource() != null) article.setNewsSource(req.getNewsSource());
        if (req.getNewsStatus() != null) article.setNewsStatus(req.getNewsStatus());

        article.setUpdatedById(updatedById);
        article.setModifiedDate(LocalDateTime.now());

        if (req.getTagIds() != null) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(req.getTagIds()));
            article.setTags(tags);
        }

        return newsArticleRepository.save(article);
    }

    public void deleteNews(String id) {
        NewsArticle article = getNewsById(id);
        newsArticleRepository.delete(article);
    }
}
