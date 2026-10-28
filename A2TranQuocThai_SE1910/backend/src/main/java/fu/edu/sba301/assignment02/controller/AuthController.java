package fu.edu.sba301.assignment02.controller;

import fu.edu.sba301.assignment02.dto.LoginRequest;
import fu.edu.sba301.assignment02.dto.LoginResponse;
import fu.edu.sba301.assignment02.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService, fu.edu.sba301.assignment02.service.SystemAccountService accountService) {
        this.accountService=accountService;
        this.authService = authService;
    }

    @GetMapping("/profile")
    public Object profile(jakarta.servlet.http.HttpServletRequest request) { return request.getAttribute("account"); }
    @PutMapping("/profile")
    public Object updateProfile(jakarta.servlet.http.HttpServletRequest request, @Valid @RequestBody fu.edu.sba301.assignment02.dto.SystemAccountRequest body) {
      var account=(fu.edu.sba301.assignment02.entity.SystemAccount)request.getAttribute("account");
      body.setAccountRole(account.getAccountRole());
      return accountService.updateAccount(account.getAccountId(),body);
    }
    private final fu.edu.sba301.assignment02.service.SystemAccountService accountService;
    @PostMapping("/logout")
    public void logout(jakarta.servlet.http.HttpSession session) { session.invalidate(); }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, jakarta.servlet.http.HttpSession session) {
        var user=authService.login(request);
        session.setAttribute("accountId", user.getAccountId());
        return ResponseEntity.ok(user);
    }
}
