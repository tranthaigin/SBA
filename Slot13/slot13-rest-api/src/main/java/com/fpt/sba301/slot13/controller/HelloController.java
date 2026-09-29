package com.fpt.sba301.slot13.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HelloController {

    @GetMapping("/api/hello")
    public Map<String, String> hello(
            @RequestParam(defaultValue = "Student") String name) {
        return Map.of(
                "message", "Hello " + name,
                "slot", "13"
        );
    }
}
