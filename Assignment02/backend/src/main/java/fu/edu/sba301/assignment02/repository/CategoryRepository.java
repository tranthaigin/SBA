package fu.edu.sba301.assignment02.repository;

import fu.edu.sba301.assignment02.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Short> {
    List<Category> findByIsActiveTrue();
    List<Category> findByCategoryNameContainingIgnoreCase(String name);
}
