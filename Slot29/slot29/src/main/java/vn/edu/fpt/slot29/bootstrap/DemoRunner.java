package vn.edu.fpt.slot29.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import vn.edu.fpt.slot29.domain.*;
import vn.edu.fpt.slot29.service.*;

@Component
@Profile("demo")
public class DemoRunner implements CommandLineRunner {
    private final IStudentService students;
    private final IBookService books;

    public DemoRunner(IStudentService students, IBookService books) {
        this.students = students;
        this.books = books;
    }

    @Override
    public void run(String... args) {
        Student student = students.createStudent(new Student("s29@fu.edu.vn", "Slot", "TwentyNine", 8.0));
        Book first = books.createBook(new Book("978-001", "Hibernate Basics", "FPT"));
        Book second = books.createBook(new Book("978-002", "ORM Design", "FPT"));
        students.borrowBooksAtomically(student.getId(), java.util.List.of(first.getId(), second.getId()));
        Student loaded = students.getStudent(student.getId());
        System.out.printf("%s has %d books%n", loaded, loaded.getBooks().size());
        students.findStudentsByBook(first.getId()).forEach(System.out::println);
    }
}
