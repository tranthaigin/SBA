package vn.edu.fpt.slot29;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.LazyInitializationException;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import vn.edu.fpt.slot29.dao.manual.ManualStudentDAO;
import vn.edu.fpt.slot29.domain.*;
import vn.edu.fpt.slot29.repository.*;
import vn.edu.fpt.slot29.service.*;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.DisplayName.class)
class StudentPersistenceIntegrationTest {
    @Autowired IStudentService students;
    @Autowired IBookService books;
    @Autowired IStudentRepository studentRepository;
    @Autowired IBookRepository bookRepository;
    @Autowired JdbcTemplate jdbc;
    @Autowired EntityManagerFactory emf;
    @Autowired PlatformTransactionManager transactionManager;

    @BeforeEach
    void cleanDatabase() {
        jdbc.update("delete from BORROWINGS");
        jdbc.update("delete from STUDENTS_BOOKS");
        jdbc.update("delete from STUDENTS");
        jdbc.update("delete from BOOKS");
    }

    @Test @DisplayName("T01 persist Student plus two Books creates two join rows")
    void persistGraph() {
        Student student = new Student("s29@fu.edu.vn", "Slot", "TwentyNine", 8.0);
        student.addBook(new Book("978-001", "Hibernate Basics", "FPT"));
        student.addBook(new Book("978-002", "ORM Design", "FPT"));
        students.createStudent(student);
        assertThat(count("STUDENTS")).isOne();
        assertThat(count("BOOKS")).isEqualTo(2);
        assertThat(count("STUDENTS_BOOKS")).isEqualTo(2);
    }

    @Test @DisplayName("T02 add existing Book creates one join row")
    void addExistingBook() {
        Student student = students.createStudent(student("t02@fu.edu.vn"));
        Book book = books.createBook(book("978-T02"));
        students.borrowBook(student.getId(), book.getId());
        assertThat(count("STUDENTS_BOOKS")).isOne();
    }

    @Test @DisplayName("T03 Set plus unique join constraint prevents duplicate semantic association")
    void setPreventsDuplicate() {
        Student student = student("t03@fu.edu.vn");
        Book first = book("978-T03");
        Book sameNaturalKey = book("978-T03");
        student.addBook(first);
        student.addBook(sameNaturalKey);
        assertThat(student.getBooks()).hasSize(1);
        students.createStudent(student);
        assertThat(count("STUDENTS_BOOKS")).isOne();
    }

    @Test @DisplayName("T04 fetch-plan keeps books available after Service returns")
    void loadWithBooks() {
        Student student = student("t04@fu.edu.vn");
        student.addBook(book("978-T04"));
        students.createStudent(student);
        Student detached = students.getStudent(student.getId());
        assertThat(detached.getBooks()).hasSize(1);
    }

    @Test @DisplayName("T05 returnBook deletes only the join row")
    void removeAssociationOnly() {
        Student student = students.createStudent(student("t05@fu.edu.vn"));
        Book book = books.createBook(book("978-T05"));
        students.borrowBook(student.getId(), book.getId());
        students.returnBook(student.getId(), book.getId());
        assertThat(count("STUDENTS_BOOKS")).isZero();
        assertThat(count("BOOKS")).isOne();
    }

    @Test @DisplayName("T06 deleting Student preserves shared Books")
    void deleteStudentPreservesBooks() {
        Book shared = books.createBook(book("978-T06"));
        Student first = students.createStudent(student("t06a@fu.edu.vn"));
        Student second = students.createStudent(student("t06b@fu.edu.vn"));
        students.borrowBook(first.getId(), shared.getId());
        students.borrowBook(second.getId(), shared.getId());
        students.deleteStudent(first.getId());
        assertThat(count("STUDENTS")).isOne();
        assertThat(count("BOOKS")).isOne();
        assertThat(count("STUDENTS_BOOKS")).isOne();
    }

    @Test @DisplayName("T07 semantic query finds Students by Book")
    void findStudentsByBook() {
        Book shared = books.createBook(book("978-T07"));
        Student first = students.createStudent(student("t07a@fu.edu.vn"));
        Student second = students.createStudent(student("t07b@fu.edu.vn"));
        students.borrowBook(first.getId(), shared.getId());
        students.borrowBook(second.getId(), shared.getId());
        assertThat(students.findStudentsByBook(shared.getId()))
                .extracting(Student::getEmail)
                .containsExactly("t07a@fu.edu.vn", "t07b@fu.edu.vn");
    }

    @Test @DisplayName("T09 inverse-only mutation does not persist a join row")
    void inverseOnlyMutation() {
        Student student = students.createStudent(student("t09@fu.edu.vn"));
        Book book = books.createBook(book("978-T09"));
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            Student managedStudent = studentRepository.findById(student.getId()).orElseThrow();
            Book managedBook = bookRepository.findById(book.getId()).orElseThrow();
            managedBook.getStudents().add(managedStudent); // intentional error: inverse side only
        });
        assertThat(count("STUDENTS_BOOKS")).isZero();
    }

    @Test @DisplayName("T10 plain findById exposes lazy failure after manual DAO closes Session")
    void lazyOutsideSession() {
        Student student = student("t10@fu.edu.vn");
        student.addBook(book("978-T10"));
        students.createStudent(student);
        SessionFactory sf = emf.unwrap(SessionFactory.class);
        Student detached = new ManualStudentDAO(sf).findById(student.getId()).orElseThrow();
        assertThatThrownBy(() -> detached.getBooks().size()).isInstanceOf(LazyInitializationException.class);
    }

    @Test @DisplayName("T11 duplicate borrow raises business exception and keeps one row")
    void duplicateBorrow() {
        Student student = students.createStudent(student("t11@fu.edu.vn"));
        Book book = books.createBook(book("978-T11"));
        students.borrowBook(student.getId(), book.getId());
        assertThatThrownBy(() -> students.borrowBook(student.getId(), book.getId()))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("already associated");
        assertThat(count("STUDENTS_BOOKS")).isOne();
    }

    @Test @DisplayName("T12 runtime failure rolls back all association changes in the use case")
    void rollbackIsAtomic() {
        Student student = students.createStudent(student("t12@fu.edu.vn"));
        Book valid = books.createBook(book("978-T12"));
        assertThatThrownBy(() -> students.borrowBooksAtomically(student.getId(), List.of(valid.getId(), 999999L)))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Book not found");
        assertThat(count("STUDENTS_BOOKS")).isZero();
    }

    private long count(String table) {
        Long value = jdbc.queryForObject("select count(*) from " + table, Long.class);
        return value == null ? 0 : value;
    }
    private Student student(String email) { return new Student(email, "Slot", "TwentyNine", 8.0); }
    private Book book(String isbn) { return new Book(isbn, "Hibernate", "FPT"); }
}
