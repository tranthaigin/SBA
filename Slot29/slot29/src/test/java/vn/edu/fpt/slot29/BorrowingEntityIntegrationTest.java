package vn.edu.fpt.slot29;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import vn.edu.fpt.slot29.domain.*;
import vn.edu.fpt.slot29.service.*;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class BorrowingEntityIntegrationTest {
    @Autowired IStudentService students;
    @Autowired IBookService books;
    @Autowired IBorrowingService borrowings;
    @Autowired JdbcTemplate jdbc;

    @BeforeEach void clean() {
        jdbc.update("delete from BORROWINGS");
        jdbc.update("delete from STUDENTS_BOOKS");
        jdbc.update("delete from STUDENTS");
        jdbc.update("delete from BOOKS");
    }

    @Test void associationEntityStoresBusinessAttributesAndCanBeReturned() {
        Student student = students.createStudent(new Student("borrowing@fu.edu.vn", "Slot", "29", 8));
        Book book = books.createBook(new Book("978-BR-01", "Association Entity", "FPT"));
        LocalDate borrowed = LocalDate.of(2026, 10, 26);
        Borrowing result = borrowings.borrow(student.getId(), book.getId(), borrowed, borrowed.plusDays(14));
        assertThat(result.getStatus()).isEqualTo(BorrowingStatus.BORROWED);
        borrowings.returnBorrowing(student.getId(), book.getId());
        assertThat(jdbc.queryForObject("select status from BORROWINGS where id=?", String.class, result.getId()))
                .isEqualTo("RETURNED");
    }

    @Test void associationEntityRejectsSecondActiveBorrowing() {
        Student student = students.createStudent(new Student("duplicate@fu.edu.vn", "Slot", "29", 8));
        Book book = books.createBook(new Book("978-BR-02", "Association Entity", "FPT"));
        LocalDate day = LocalDate.of(2026, 10, 26);
        borrowings.borrow(student.getId(), book.getId(), day, day.plusDays(14));
        assertThatThrownBy(() -> borrowings.borrow(student.getId(), book.getId(), day.plusDays(1), day.plusDays(15)))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("active borrowing");
    }
}
