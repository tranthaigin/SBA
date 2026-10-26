package vn.edu.fpt.slot29.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.slot29.domain.*;
import vn.edu.fpt.slot29.repository.*;
import java.time.LocalDate;

@Service
@Transactional
public class BorrowingService implements IBorrowingService {
    private final IStudentRepository students;
    private final IBookRepository books;
    private final IBorrowingRepository borrowings;

    public BorrowingService(IStudentRepository students, IBookRepository books, IBorrowingRepository borrowings) {
        this.students = students;
        this.books = books;
        this.borrowings = borrowings;
    }

    @Override public Borrowing borrow(Long studentId, Long bookId, LocalDate borrowedDate, LocalDate dueDate) {
        if (borrowings.findActive(studentId, bookId).isPresent())
            throw new IllegalStateException("An active borrowing already exists");
        Student student = students.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));
        Book book = books.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));
        return borrowings.save(new Borrowing(student, book, borrowedDate, dueDate));
    }

    @Override public void returnBorrowing(Long studentId, Long bookId) {
        Borrowing borrowing = borrowings.findActive(studentId, bookId)
                .orElseThrow(() -> new IllegalStateException("Active borrowing not found"));
        borrowing.markReturned();
    }
}
