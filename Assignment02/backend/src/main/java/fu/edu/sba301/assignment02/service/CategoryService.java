package fu.edu.sba301.assignment02.service;

import fu.edu.sba301.assignment02.dto.CategoryRequest;
import fu.edu.sba301.assignment02.entity.Category;
import fu.edu.sba301.assignment02.exception.BusinessRuleViolationException;
import fu.edu.sba301.assignment02.exception.ResourceNotFoundException;
import fu.edu.sba301.assignment02.repository.CategoryRepository;
import fu.edu.sba301.assignment02.repository.NewsArticleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final NewsArticleRepository newsArticleRepository;

    public CategoryService(CategoryRepository categoryRepository, NewsArticleRepository newsArticleRepository) {
        this.categoryRepository = categoryRepository;
        this.newsArticleRepository = newsArticleRepository;
    }

    public List<Category> getAllCategories(String search) {
        if (search != null && !search.isBlank()) {
            return categoryRepository.findByCategoryNameContainingIgnoreCase(search.trim());
        }
        return categoryRepository.findAll();
    }

    public List<Category> getActiveCategories() {
        return categoryRepository.findByIsActiveTrue();
    }

    public Category getCategoryById(Short id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
    }

    public Category createCategory(CategoryRequest req) {
        Category cat = new Category();
        cat.setCategoryName(req.getCategoryName().trim());
        cat.setCategoryDesciption(req.getCategoryDesciption().trim());
        cat.setParentCategoryId(req.getParentCategoryId());
        cat.setIsActive(req.getIsActive() != null ? req.getIsActive() : true);
        return categoryRepository.save(cat);
    }

    public Category updateCategory(Short id, CategoryRequest req) {
        Category cat = getCategoryById(id);
        cat.setCategoryName(req.getCategoryName().trim());
        cat.setCategoryDesciption(req.getCategoryDesciption().trim());
        cat.setParentCategoryId(req.getParentCategoryId());
        cat.setIsActive(req.getIsActive() != null ? req.getIsActive() : true);
        return categoryRepository.save(cat);
    }

    public void deleteCategory(Short id) {
        Category cat = getCategoryById(id);
        if (newsArticleRepository.existsByCategory(cat)) {
            throw new BusinessRuleViolationException("Cannot delete Category because it is associated with existing news articles");
        }
        categoryRepository.delete(cat);
    }
}
