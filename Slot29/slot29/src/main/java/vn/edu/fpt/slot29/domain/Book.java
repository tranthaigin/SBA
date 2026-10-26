package vn.edu.fpt.slot29.domain;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "BOOKS")
public class Book {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 30)
    private String isbn;
    @Column(nullable = false, length = 200)
    private String title;
    @Column(nullable = false, length = 120)
    private String author;

    @ManyToMany(mappedBy = "books", fetch = FetchType.LAZY)
    private Set<Student> students = new HashSet<>();
    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    private Set<Borrowing> borrowings = new HashSet<>();

    protected Book() { }
    public Book(String isbn, String title, String author) {
        this.isbn = requireText(isbn, "isbn");
        this.title = requireText(title, "title");
        this.author = requireText(author, "author");
    }

    void addStudentInternal(Student student) { students.add(Objects.requireNonNull(student)); }
    void removeStudentInternal(Student student) { students.remove(Objects.requireNonNull(student)); }
    void addBorrowingInternal(Borrowing borrowing) { borrowings.add(borrowing); }
    public Long getId() { return id; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public Set<Student> getStudents() { return students; }
    public Set<Borrowing> getBorrowings() { return Collections.unmodifiableSet(borrowings); }

    @Override public boolean equals(Object other) {
        return this == other || other instanceof Book that && isbn != null && isbn.equals(that.isbn);
    }
    @Override public int hashCode() { return Book.class.hashCode(); }
    @Override public String toString() {
        return "Book{id=" + id + ", isbn='" + isbn + "', title='" + title + "'}";
    }
    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " must not be blank");
        return value.trim();
    }
}
