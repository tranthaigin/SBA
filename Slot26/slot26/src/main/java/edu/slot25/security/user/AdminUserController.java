package edu.slot25.security.user;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AppUserRepository users;

    public AdminUserController(AppUserRepository users) {
        this.users = users;
    }

    @GetMapping
    public List<UserResponse> findAll() {
        return users.findAll().stream()
                .map(user -> new UserResponse(user.getId(), user.getEmail(), user.getRole()))
                .toList();
    }
}
