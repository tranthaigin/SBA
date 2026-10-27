package fu.edu.sba301.slot30;

import fu.edu.sba301.slot30.dao.StudentDAO;
import fu.edu.sba301.slot30.entity.Student;
import fu.edu.sba301.slot30.repository.IStudentRepository;
import fu.edu.sba301.slot30.repository.StudentRepository;
import fu.edu.sba301.slot30.service.IStudentService;
import fu.edu.sba301.slot30.service.StudentService;
import fu.edu.sba301.slot30.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HibernateCrudIntegrationTest {

    private static SessionFactory sessionFactory;
    private static StudentDAO studentDAO;
    private static IStudentRepository studentRepository;
    private static IStudentService studentService;

    @BeforeAll
    static void setup() {
        sessionFactory = HibernateUtil.getSessionFactory();
        studentDAO = new StudentDAO(sessionFactory);
        studentRepository = new StudentRepository(studentDAO);
        studentService = new StudentService(studentRepository);
    }

    @AfterAll
    static void tearDown() {
        HibernateUtil.shutdown();
    }

    @BeforeEach
    void cleanDatabase() {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createMutationQuery("delete from Student").executeUpdate();
            session.createMutationQuery("delete from Book").executeUpdate();
            tx.commit();
        }
    }

    @Test
    void t01_createAndFindById() {
        Student s = new Student("sv1@fpt.edu.vn", "Minh", "Le", 8.0);
        Student created = studentService.create(s);

        assertThat(created.getId()).isNotNull();

        Student retrieved = studentService.getById(created.getId());
        assertThat(retrieved.getEmail()).isEqualTo("sv1@fpt.edu.vn");
        assertThat(retrieved.getFirstName()).isEqualTo("Minh");
        assertThat(retrieved.getLastName()).isEqualTo("Le");
        assertThat(retrieved.getMarks()).isEqualTo(8.0);
    }

    @Test
    void t02_findAllReturnsAllStudents() {
        studentService.create(new Student("a@fpt.edu.vn", "A", "Nguyen", 7.0));
        studentService.create(new Student("b@fpt.edu.vn", "B", "Tran", 8.0));
        studentService.create(new Student("c@fpt.edu.vn", "C", "Pham", 9.0));

        List<Student> list = studentService.getAll();
        assertThat(list).hasSize(3);
    }

    @Test
    void t03_findByEmailReturnsMatchingStudent() {
        studentService.create(new Student("target@fpt.edu.vn", "Target", "User", 6.5));

        Student found = studentRepository.findByEmail("target@fpt.edu.vn").orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getFirstName()).isEqualTo("Target");

        assertThat(studentRepository.findByEmail("nonexistent@fpt.edu.vn")).isEmpty();
    }

    @Test
    void t04_updateMarksUsingDirtyChecking() {
        Student s = studentService.create(new Student("update@fpt.edu.vn", "Up", "Date", 5.0));

        studentService.updateMarks(s.getId(), 9.5);

        Student reloaded = studentService.getById(s.getId());
        assertThat(reloaded.getMarks()).isEqualTo(9.5);
    }

    @Test
    void t05_updateDetachedEntityMerge() {
        Student s = studentService.create(new Student("merge@fpt.edu.vn", "Original", "Name", 6.0));

        Student detached = new Student("merge@fpt.edu.vn", "Modified", "Name", 7.5);
        detached.setId(s.getId());

        Student updated = studentService.update(detached);
        assertThat(updated.getFirstName()).isEqualTo("Modified");
        assertThat(updated.getMarks()).isEqualTo(7.5);

        Student reloaded = studentService.getById(s.getId());
        assertThat(reloaded.getFirstName()).isEqualTo("Modified");
    }

    @Test
    void t06_deleteRemovesStudent() {
        Student s = studentService.create(new Student("delete@fpt.edu.vn", "Del", "Ete", 4.0));
        Long id = s.getId();

        studentService.delete(id);

        assertThatThrownBy(() -> studentService.getById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void t07_createRejectsDuplicateEmail() {
        studentService.create(new Student("dup@fpt.edu.vn", "First", "Student", 8.0));

        assertThatThrownBy(() -> studentService.create(new Student("dup@fpt.edu.vn", "Second", "Student", 7.0)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Email already exists");
    }

    @Test
    void t08_validationRejectsInvalidMarks() {
        assertThatThrownBy(() -> studentService.create(new Student("mark@fpt.edu.vn", "Invalid", "Mark", 11.0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("between 0.0 and 10.0");

        assertThatThrownBy(() -> studentService.create(new Student("mark2@fpt.edu.vn", "Invalid", "Mark", -1.0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("between 0.0 and 10.0");
    }

    @Test
    void t09_validationRejectsBlankFields() {
        assertThatThrownBy(() -> studentService.create(new Student("", "John", "Doe", 7.0)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> studentService.create(new Student("not-an-email", "John", "Doe", 7.0)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> studentService.create(new Student("valid@fpt.edu.vn", "", "Doe", 7.0)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
