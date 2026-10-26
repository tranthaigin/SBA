package vn.edu.fpt.slot29.repository;

import vn.edu.fpt.slot29.domain.Student;
import java.util.*;

public interface IStudentRepository {
    Student save(Student student);
    List<Student> findAll();
    Optional<Student> findById(Long id);
    Optional<Student> findByIdWithBooks(Long id);
    Student update(Student student);
    boolean delete(Long id);
    List<Student> findStudentsByBook(Long bookId);
}
