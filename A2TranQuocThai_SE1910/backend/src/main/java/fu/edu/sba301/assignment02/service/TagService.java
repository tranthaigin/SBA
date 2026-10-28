package fu.edu.sba301.assignment02.service;
import fu.edu.sba301.assignment02.entity.Tag;
import fu.edu.sba301.assignment02.repository.TagRepository;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class TagService {
 private final TagRepository repository;
 public TagService(TagRepository repository){this.repository=repository;}
 public List<Tag> getAll(){return repository.findAll();}
}
