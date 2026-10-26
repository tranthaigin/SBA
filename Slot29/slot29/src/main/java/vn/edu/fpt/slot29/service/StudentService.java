package vn.edu.fpt.slot29.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.slot29.domain.*;
import vn.edu.fpt.slot29.repository.*;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class StudentService implements IStudentService {
    private final IStudentRepository students;
    private final IBookRepository books;

    public StudentService(IStudentRepository students, IBookRepository books) {
        this.students = students;
        this.books = books;
    }

    @Override public List<Student> getStudents() { return students.findAll(); }

    @Override public Student getStudent(Long id) {
        return students.findByIdWithBooks(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + id));
    }

    @Override @Transactional
    public Student createStudent(Student student) { return students.save(student); }

    @Override @Transactional
    public Student updateStudent(Student student) { return students.update(student); }

    @Override @Transactional
    public void deleteStudent(Long id) {
        if (!students.delete(id)) throw new IllegalArgumentException("Student not found: " + id);
    }

    @Override @Transactional
    public void borrowBook(Long studentId, Long bookId) {
        Student student = requiredStudentWithBooks(studentId);
        Book book = requiredBook(bookId);
        associate(student, book);
    }

    @Override @Transactional
    public void returnBook(Long studentId, Long bookId) {
        Student student = requiredStudentWithBooks(studentId);
        Book book = student.getBooks().stream().filter(b -> Objects.equals(b.getId(), bookId))
                .findFirst().orElseThrow(() -> new IllegalStateException("Association not found"));
        student.removeBook(book);
    }

    @Override public List<Student> findStudentsByBook(Long bookId) {
        return students.findStudentsByBook(bookId);
    }

    @Override @Transactional
    public void borrowBooksAtomically(Long studentId, List<Long> bookIds) {
        Student student = requiredStudentWithBooks(studentId);
        for (Long bookId : bookIds) associate(student, requiredBook(bookId));
    }

    private Student requiredStudentWithBooks(Long id) {
        return students.findByIdWithBooks(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + id));
    }

    private Book requiredBook(Long id) {
        return books.findById(id).orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));
    }

    private void associate(Student student, Book book) {
        boolean duplicate = student.getBooks().stream().anyMatch(b -> Objects.equals(b.getId(), book.getId()));
        if (duplicate) throw new IllegalStateException("Book already associated");
        student.addBook(book);
        // Both entities are managed in this transaction; dirty checking writes the join row.
    }
}
