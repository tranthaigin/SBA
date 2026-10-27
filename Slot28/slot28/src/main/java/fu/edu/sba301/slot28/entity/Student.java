package fu.edu.sba301.slot28.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "STUDENTS")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(nullable = false, length = 80)
    private String firstName;

    @Column(nullable = false, length = 80)
    private String lastName;

    private double marks;

    @ManyToMany
    @JoinTable(
            name = "STUDENTS_BOOKS",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private Set<Book> books = new HashSet<>();

    public Student() {
        // Required by Hibernate.
    }

    public Student(String email, String firstName, String lastName, double marks) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.marks = marks;
    }

    public void addBook(Book book) {
        books.add(book);
        book.getStudents().add(this);
    }

    public void removeBook(Book book) {
        books.remove(book);
        book.getStudents().remove(this);
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public double getMarks() { return marks; }
    public void setMarks(double marks) { this.marks = marks; }
    public Set<Book> getBooks() { return books; }

    @Override
    public String toString() {
        return "Student{id=%d, email='%s', firstName='%s', lastName='%s', marks=%.1f}"
                .formatted(id, email, firstName, lastName, marks);
    }
}
