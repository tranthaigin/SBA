package fu.edu.sba301.assignment02.controller;

import fu.edu.sba301.assignment02.entity.Tag;
import fu.edu.sba301.assignment02.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@CrossOrigin(origins = "*")
public class TagController {

    private final TagService tagRepository;

    public TagController(TagService tagRepository) {
        this.tagRepository = tagRepository;
    }

    @GetMapping
    public ResponseEntity<List<Tag>> getAll() {
        return ResponseEntity.ok(tagRepository.getAll());
    }
}
