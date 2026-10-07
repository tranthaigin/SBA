package com.example.orchid.controllers;

import com.example.orchid.pojos.Orchid;
import com.example.orchid.services.IOrchidService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/orchids", "/orchids"})
@CrossOrigin(origins = "*")
public class OrchidController {

    private final IOrchidService orchidService;

    public OrchidController(IOrchidService orchidService) {
        this.orchidService = orchidService;
    }

    @GetMapping({"", "/"})
    public List<Orchid> getAll(@RequestParam(required = false) String name) {
        if (name != null && !name.isBlank()) {
            return orchidService.searchByName(name);
        }
        return orchidService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Orchid> getById(@PathVariable Long id) {
        return orchidService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping({"", "/"})
    public ResponseEntity<?> create(@Valid @RequestBody Orchid orchid) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(orchidService.create(orchid));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody Orchid orchid) {
        try {
            var updated = orchidService.update(id, orchid);
            if (updated.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updated.get());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return orchidService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
