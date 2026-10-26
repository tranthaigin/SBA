package vn.edu.fpt.slot29.service;

import vn.edu.fpt.slot29.domain.Borrowing;
import java.time.LocalDate;

public interface IBorrowingService {
    Borrowing borrow(Long studentId, Long bookId, LocalDate borrowedDate, LocalDate dueDate);
    void returnBorrowing(Long studentId, Long bookId);
}
