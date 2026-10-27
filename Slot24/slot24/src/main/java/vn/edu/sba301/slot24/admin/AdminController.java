package vn.edu.sba301.slot24.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AdminController {

    @GetMapping("/admin/health")
    public Map<String, String> health() {
        return Map.of("status", "admin-only");
    }
}
