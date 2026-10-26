package vn.edu.fpt.slot29.repository;

import org.springframework.stereotype.Repository;
import vn.edu.fpt.slot29.dao.StudentDAO;
import vn.edu.fpt.slot29.domain.Student;
import java.util.*;

@Repository
public class StudentRepository implements IStudentRepository {
    private final StudentDAO dao;
    public StudentRepository(StudentDAO dao) { this.dao = dao; }
    @Override public Student save(Student student) { return dao.save(student); }
    @Override public List<Student> findAll() { return dao.findAll(); }
    @Override public Optional<Student> findById(Long id) { return dao.findById(id); }
    @Override public Optional<Student> findByIdWithBooks(Long id) { return dao.findByIdWithBooks(id); }
    @Override public Student update(Student student) { return dao.update(student); }
    @Override public boolean delete(Long id) { return dao.delete(id); }
    @Override public List<Student> findStudentsByBook(Long bookId) { return dao.findStudentsByBook(bookId); }
}
