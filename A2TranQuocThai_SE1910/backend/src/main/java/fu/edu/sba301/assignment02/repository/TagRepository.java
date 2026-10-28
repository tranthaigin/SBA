package fu.edu.sba301.assignment02.repository;

import fu.edu.sba301.assignment02.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
}
