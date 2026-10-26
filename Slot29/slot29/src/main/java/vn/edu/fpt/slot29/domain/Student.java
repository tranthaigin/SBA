package vn.edu.fpt.slot29.domain;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "STUDENTS")
public class Student {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 150)
    private String email;
    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;
    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;
    @Column(nullable = false)
    private double gpa;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "STUDENTS_BOOKS",
            joinColumns = @JoinColumn(name = "student_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "book_id", nullable = false),
            uniqueConstraints = @UniqueConstraint(name = "UK_STUDENT_BOOK", columnNames = {"student_id", "book_id"}))
    private Set<Book> books = new HashSet<>();

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private Set<Borrowing> borrowings = new HashSet<>();

    protected Student() { }

    public Student(String email, String firstName, String lastName, double gpa) {
        this.email = requireText(email, "email");
        this.firstName = requireText(firstName, "firstName");
        this.lastName = requireText(lastName, "lastName");
        this.gpa = gpa;
    }

    public void addBook(Book book) {
        Objects.requireNonNull(book, "book must not be null");
        if (books.add(book)) book.addStudentInternal(this);
    }

    public void removeBook(Book book) {
        Objects.requireNonNull(book, "book must not be null");
        if (books.remove(book)) book.removeStudentInternal(this);
    }

    void addBorrowingInternal(Borrowing borrowing) { borrowings.add(borrowing); }
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public double getGpa() { return gpa; }
    public Set<Book> getBooks() { return books; }
    public Set<Borrowing> getBorrowings() { return Collections.unmodifiableSet(borrowings); }

    public void changeProfile(String firstName, String lastName, double gpa) {
        this.firstName = requireText(firstName, "firstName");
        this.lastName = requireText(lastName, "lastName");
        this.gpa = gpa;
    }

    @Override public boolean equals(Object other) {
        return this == other || other instanceof Student that && email != null && email.equals(that.email);
    }
    @Override public int hashCode() { return Student.class.hashCode(); }
    @Override public String toString() {
        return "Student{id=" + id + ", email='" + email + "', name='" + firstName + " " + lastName + "'}";
    }
    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " must not be blank");
        return value.trim();
    }
}
