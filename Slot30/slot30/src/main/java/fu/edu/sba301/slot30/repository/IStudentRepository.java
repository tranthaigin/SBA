package fu.edu.sba301.slot30.repository;

import fu.edu.sba301.slot30.entity.Student;

import java.util.List;
import java.util.Optional;

public interface IStudentRepository {
    Student save(Student student);
    List<Student> findAll();
    Optional<Student> findById(Long id);
    Optional<Student> findByEmail(String email);
    Student update(Student student);
    Student updateMarks(Long id, double marks);
    boolean delete(Long id);
}
