package vn.edu.sba301.slot24.orchid;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orchids")
public class OrchidController {

    private final OrchidService orchidService;

    public OrchidController(OrchidService orchidService) {
        this.orchidService = orchidService;
    }

    @GetMapping
    public List<Orchid> findAll() {
        return orchidService.findAll();
    }

    @GetMapping("/{id}")
    public Orchid findById(@PathVariable long id) {
        return orchidService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Orchid create(@Valid @RequestBody OrchidRequest request) {
        return orchidService.create(request);
    }

    @PutMapping("/{id}")
    public Orchid update(@PathVariable long id, @Valid @RequestBody OrchidRequest request) {
        return orchidService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        orchidService.delete(id);
    }
}
