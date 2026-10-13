package fu.se.chapter15api.controllers;

import fu.se.chapter15api.pojos.Orchid;
import fu.se.chapter15api.services.IOrchidService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orchids")
public class OrchidController {
    private final IOrchidService service;

    public OrchidController(IOrchidService service) {
        this.service = service;
    }

    @GetMapping("/")
    public ResponseEntity<List<Orchid>> fetchAll() {
        return ResponseEntity.ok(service.getAllOrchids());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Orchid> getById(@PathVariable int id) {
        return service.getOrchidByID(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public ResponseEntity<Orchid> saveOrchid(@RequestBody Orchid orchid) {
        Orchid created = service.insertOrchid(orchid);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Orchid> updateOrchid(@PathVariable int id, @RequestBody Orchid orchid) {
        try {
            return ResponseEntity.ok(service.updateOrchid(id, orchid));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrchid(@PathVariable int id) {
        if (service.getOrchidByID(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.deleteOrchid(id);
        return ResponseEntity.noContent().build();
    }
}
