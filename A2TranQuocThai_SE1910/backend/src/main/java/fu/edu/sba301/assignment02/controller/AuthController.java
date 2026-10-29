package fu.edu.sba301.assignment02.controller;

import fu.edu.sba301.assignment02.dto.LoginRequest;
import fu.edu.sba301.assignment02.dto.LoginResponse;
import fu.edu.sba301.assignment02.dto.SystemAccountRequest;
import fu.edu.sba301.assignment02.entity.SystemAccount;
import fu.edu.sba301.assignment02.service.AuthService;
import fu.edu.sba301.assignment02.service.SystemAccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller xử lý xác thực người dùng (Login, Logout) và quản lý hồ sơ cá nhân.
 * Quản lý phiên làm việc thông qua HttpSession.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final SystemAccountService accountService;

    public AuthController(AuthService authService, SystemAccountService accountService) {
        this.accountService = accountService;
        this.authService = authService;
    }

    /**
     * Lấy thông tin tài khoản người dùng đang đăng nhập từ HttpServletRequest attribute.
     */
    @GetMapping("/profile")
    public Object profile(HttpServletRequest request) {
        return request.getAttribute("account");
    }

    /**
     * Cập nhật thông tin hồ sơ của tài khoản đang đăng nhập, giữ nguyên quyền hạn ban đầu.
     */
    @PutMapping("/profile")
    public Object updateProfile(HttpServletRequest request, @Valid @RequestBody SystemAccountRequest body) {
        var account = (SystemAccount) request.getAttribute("account");
        body.setAccountRole(account.getAccountRole());
        return accountService.updateAccount(account.getAccountId(), body);
    }

    /**
     * Đăng xuất người dùng và hủy bỏ toàn bộ phiên làm việc HttpSession hiện tại.
     */
    @PostMapping("/logout")
    public void logout(HttpSession session) {
        session.invalidate();
    }

    /**
     * Xác thực thông tin đăng nhập (email và password), tạo phiên đăng nhập session và trả về thông tin user.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        var user = authService.login(request);
        session.setAttribute("accountId", user.getAccountId());
        return ResponseEntity.ok(user);
    }
}
