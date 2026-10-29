package fu.edu.sba301.assignment02.controller;

import fu.edu.sba301.assignment02.dto.SystemAccountRequest;
import fu.edu.sba301.assignment02.entity.SystemAccount;
import fu.edu.sba301.assignment02.service.SystemAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller cung cấp RESTful API quản lý Tài khoản người dùng (SystemAccount).
 * Chỉ dành riêng cho vai trò Quản trị viên (Admin - Role 1).
 */
@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class SystemAccountController {

    private final SystemAccountService accountService;

    public SystemAccountController(SystemAccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Lấy danh sách toàn bộ tài khoản người dùng hoặc tìm kiếm theo tên / email.
     */
    @GetMapping
    public ResponseEntity<List<SystemAccount>> getAll(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(accountService.getAllAccounts(search));
    }

    /**
     * Lấy thông tin chi tiết của một tài khoản người dùng theo ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SystemAccount> getById(@PathVariable Short id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    /**
     * Tạo mới một tài khoản người dùng với kiểm tra ràng buộc duy nhất email.
     */
    @PostMapping
    public ResponseEntity<SystemAccount> create(@Valid @RequestBody SystemAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(request));
    }

    /**
     * Cập nhật thông tin tài khoản người dùng (tên, email, vai trò, mật khẩu mới).
     */
    @PutMapping("/{id}")
    public ResponseEntity<SystemAccount> update(@PathVariable Short id, @Valid @RequestBody SystemAccountRequest request) {
        return ResponseEntity.ok(accountService.updateAccount(id, request));
    }

    /**
     * Xóa tài khoản người dùng; kiểm tra và chặn xóa nếu tài khoản đó đã từng tạo bài viết tin tức.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Short id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
