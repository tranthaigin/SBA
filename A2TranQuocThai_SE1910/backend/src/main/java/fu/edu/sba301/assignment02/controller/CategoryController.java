package fu.edu.sba301.assignment02.controller;

import fu.edu.sba301.assignment02.dto.CategoryRequest;
import fu.edu.sba301.assignment02.entity.Category;
import fu.edu.sba301.assignment02.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller cung cấp các RESTful API phục vụ quản lý Thể loại (Category).
 * Hỗ trợ các thao tác CRUD và tìm kiếm thể loại cho phân hệ Staff.
 */
@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Lấy toàn bộ danh sách thể loại hoặc lọc theo từ khóa tìm kiếm.
     */
    @GetMapping
    public ResponseEntity<List<Category>> getAll(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(categoryService.getAllCategories(search));
    }

    /**
     * Lấy danh sách các thể loại đang kích hoạt (Active) dành cho giao diện công khai và chọn lựa.
     */
    @GetMapping("/active")
    public ResponseEntity<List<Category>> getActive() {
        return ResponseEntity.ok(categoryService.getActiveCategories());
    }

    /**
     * Lấy chi tiết thông tin một thể loại dựa trên mã định danh ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Category> getById(@PathVariable Short id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    /**
     * Thêm mới một thể loại sau khi kiểm tra tính hợp lệ của dữ liệu đầu vào.
     */
    @PostMapping
    public ResponseEntity<Category> create(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(request));
    }

    /**
     * Cập nhật thông tin của thể loại theo ID với dữ liệu mới từ request body.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Category> update(@PathVariable Short id, @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    /**
     * Xóa thể loại theo ID; hệ thống sẽ kiểm tra và chặn xóa nếu thể loại đang có bài viết liên kết.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Short id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
