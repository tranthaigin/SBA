package vn.edu.sba301.slot24.user;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserController {

    @GetMapping("/users/me")
    public Map<String, Object> me(Authentication authentication) {
        return Map.of(
                "name", authentication.getName(),
                "authorities", authentication.getAuthorities().stream()
                        .map(authority -> authority.getAuthority())
                        .toList()
        );
    }
}
