package fu.edu.sba301.assignment02.repository;

import fu.edu.sba301.assignment02.entity.Category;
import fu.edu.sba301.assignment02.entity.NewsArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, String> {
    List<NewsArticle> findByNewsStatusTrue();
    List<NewsArticle> findByCreatedById(Short createdById);
    boolean existsByCategory(Category category);
    boolean existsByCreatedById(Short createdById);
    List<NewsArticle> findByNewsTitleContainingIgnoreCaseOrHeadlineContainingIgnoreCase(String title, String headline);
}
