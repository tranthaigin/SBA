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

/**
 * Service xử lý các nghiệp vụ logic cho Thể loại (Category).
 * Đảm bảo kiểm tra toàn vẹn tham chiếu và quy tắc nghiệp vụ hệ thống.
 */
@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final NewsArticleRepository newsArticleRepository;

    public CategoryService(CategoryRepository categoryRepository, NewsArticleRepository newsArticleRepository) {
        this.categoryRepository = categoryRepository;
        this.newsArticleRepository = newsArticleRepository;
    }

    /**
     * Lấy toàn bộ danh sách thể loại hoặc lọc theo từ khóa tên không phân biệt hoa thường.
     */
    public List<Category> getAllCategories(String search) {
        if (search != null && !search.isBlank()) {
            return categoryRepository.findByCategoryNameContainingIgnoreCase(search.trim());
        }
        return categoryRepository.findAll();
    }

    /**
     * Lấy danh sách các thể loại đang hoạt động (isActive = true).
     */
    public List<Category> getActiveCategories() {
        return categoryRepository.findByIsActiveTrue();
    }

    /**
     * Tìm thể loại theo ID, ném ngoại lệ ResourceNotFoundException nếu không tìm thấy.
     */
    public Category getCategoryById(Short id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
    }

    /**
     * Tạo mới thể loại tin tức và lưu trữ vào cơ sở dữ liệu SQL Server.
     */
    public Category createCategory(CategoryRequest req) {
        Category cat = new Category();
        cat.setCategoryName(req.getCategoryName().trim());
        cat.setCategoryDesciption(req.getCategoryDesciption().trim());
        cat.setParentCategoryId(req.getParentCategoryId());
        cat.setIsActive(req.getIsActive() != null ? req.getIsActive() : true);
        return categoryRepository.save(cat);
    }

    /**
     * Cập nhật thông tin của thể loại đã có sẵn trong cơ sở dữ liệu.
     */
    public Category updateCategory(Short id, CategoryRequest req) {
        Category cat = getCategoryById(id);
        cat.setCategoryName(req.getCategoryName().trim());
        cat.setCategoryDesciption(req.getCategoryDesciption().trim());
        cat.setParentCategoryId(req.getParentCategoryId());
        cat.setIsActive(req.getIsActive() != null ? req.getIsActive() : true);
        return categoryRepository.save(cat);
    }

    /**
     * Xóa thể loại; ngăn chặn xóa nếu thể loại đang có bài viết liên kết theo quy tắc nghiệp vụ đề bài.
     */
    public void deleteCategory(Short id) {
        Category cat = getCategoryById(id);
        if (newsArticleRepository.existsByCategory(cat)) {
            throw new BusinessRuleViolationException("Cannot delete Category because it is associated with existing news articles");
        }
        categoryRepository.delete(cat);
    }
}
