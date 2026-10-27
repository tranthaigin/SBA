package fu.edu.sba301.slot28.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "BOOKS")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String isbn;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 120)
    private String author;

    @ManyToMany(mappedBy = "books")
    private Set<Student> students = new HashSet<>();

    public Book() {
        // Required by Hibernate.
    }

    public Book(String isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }

    public Long getId() { return id; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public Set<Student> getStudents() { return students; }

    @Override
    public String toString() {
        return "Book{id=%d, isbn='%s', title='%s', author='%s'}"
                .formatted(id, isbn, title, author);
    }
}
