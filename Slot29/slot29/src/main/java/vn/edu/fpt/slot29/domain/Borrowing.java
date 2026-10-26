package vn.edu.fpt.slot29.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "BORROWINGS", uniqueConstraints = @UniqueConstraint(
        name = "UK_BORROWING_STUDENT_BOOK_DATE",
        columnNames = {"student_id", "book_id", "borrowed_date"}))
public class Borrowing {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    @Column(name = "borrowed_date", nullable = false)
    private LocalDate borrowedDate;
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BorrowingStatus status;

    protected Borrowing() { }
    public Borrowing(Student student, Book book, LocalDate borrowedDate, LocalDate dueDate) {
        this.student = Objects.requireNonNull(student);
        this.book = Objects.requireNonNull(book);
        this.borrowedDate = Objects.requireNonNull(borrowedDate);
        this.dueDate = Objects.requireNonNull(dueDate);
        if (dueDate.isBefore(borrowedDate)) throw new IllegalArgumentException("dueDate must not precede borrowedDate");
        this.status = BorrowingStatus.BORROWED;
        student.addBorrowingInternal(this);
        book.addBorrowingInternal(this);
    }

    public void markReturned() {
        if (status != BorrowingStatus.BORROWED && status != BorrowingStatus.OVERDUE) {
            throw new IllegalStateException("Borrowing is not active");
        }
        status = BorrowingStatus.RETURNED;
    }
    public Long getId() { return id; }
    public Student getStudent() { return student; }
    public Book getBook() { return book; }
    public LocalDate getBorrowedDate() { return borrowedDate; }
    public LocalDate getDueDate() { return dueDate; }
    public BorrowingStatus getStatus() { return status; }
}
