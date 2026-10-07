package com.example.orchid.controllers;

import com.example.orchid.pojos.OrchidCategory;
import com.example.orchid.services.OrchidCategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/categories", "/categories"})
@CrossOrigin(origins = "*")
public class OrchidCategoryController {

    private final OrchidCategoryService categoryService;

    public OrchidCategoryController(OrchidCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<OrchidCategory> getAll() {
        return categoryService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrchidCategory> getById(@PathVariable Long id) {
        return categoryService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody OrchidCategory category) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(category));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
