package vn.edu.fpt.slot29.service;

import vn.edu.fpt.slot29.domain.Student;
import java.util.List;

public interface IStudentService {
    List<Student> getStudents();
    Student getStudent(Long id);
    Student createStudent(Student student);
    Student updateStudent(Student student);
    void deleteStudent(Long id);
    void borrowBook(Long studentId, Long bookId);
    void returnBook(Long studentId, Long bookId);
    List<Student> findStudentsByBook(Long bookId);
    void borrowBooksAtomically(Long studentId, List<Long> bookIds);
}
