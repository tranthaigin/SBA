package fu.edu.sba301.slot28.app;

import fu.edu.sba301.slot28.entity.Book;
import fu.edu.sba301.slot28.entity.Student;
import fu.edu.sba301.slot28.repository.StudentRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class WorkbookRunner {
    private final SessionFactory sessionFactory;
    private final StudentRepository students;

    public WorkbookRunner(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.students = new StudentRepository(sessionFactory);
    }

    public void run(String experiment, long studentId, int batchSize) {
        switch (experiment.toLowerCase(Locale.ROOT)) {
            case "seed", "lifecycle" -> lifecycle();
            case "dirty-checking" -> dirtyChecking(studentId);
            case "first-level-cache" -> firstLevelCache(studentId);
            case "detached-merge" -> detachedAndMerge(studentId);
            case "hql" -> hqlExperiments(studentId);
            case "relationship" -> relationship();
            case "rollback" -> rollback();
            case "break-hql" -> breakHql();
            case "batch" -> batch(batchSize);
            default -> throw new IllegalArgumentException("Unknown experiment: " + experiment);
        }
    }

    private void lifecycle() {
        Student student = new Student(uniqueEmail("alice"), "Alice", "Nguyen", 8.5);
        System.out.println("TRANSIENT: id=" + student.getId());
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(student);
                System.out.println("PERSISTENT: id=" + student.getId()
                        + ", managed=" + session.contains(student));
                transaction.commit();
            } catch (RuntimeException exception) {
                rollback(transaction);
                throw exception;
            }
        }
        System.out.println("DETACHED: id=" + student.getId() + " (Session closed)");
        System.out.println("PASS: INSERT committed. Reuse --lab.student-id=" + student.getId());
    }

    private void dirtyChecking(long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Student student = requireStudent(session, id);
                double oldMarks = student.getMarks();
                student.setMarks(9.0);
                transaction.commit();
                System.out.printf("PASS: %.1f -> %.1f; inspect one UPDATE at commit.%n",
                        oldMarks, student.getMarks());
            } catch (RuntimeException exception) {
                rollback(transaction);
                throw exception;
            }
        }
    }

    private void firstLevelCache(long id) {
        try (Session session = sessionFactory.openSession()) {
            Student first = requireStudent(session, id);
            Student second = requireStudent(session, id);
            System.out.println("a == b: " + (first == second));
            System.out.println("PASS: same reference; inspect exactly one SELECT for Student id=" + id);
        }
    }

    private void detachedAndMerge(long id) {
        Student detached;
        try (Session session = sessionFactory.openSession()) {
            detached = requireStudent(session, id);
        }

        double databaseValue = detached.getMarks();
        detached.setMarks(7.0);
        double valueBeforeMerge = students.findById(id).orElseThrow().getMarks();
        System.out.printf("DETACHED: object=%.1f, database=%.1f (expected unchanged: %.1f)%n",
                detached.getMarks(), valueBeforeMerge, databaseValue);

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Student managed = session.merge(detached);
                managed.setMarks(8.8);
                System.out.println("original managed? " + session.contains(detached));
                System.out.println("merge result managed? " + session.contains(managed));
                transaction.commit();
            } catch (RuntimeException exception) {
                rollback(transaction);
                throw exception;
            }
        }
        System.out.println("PASS: merge result committed marks=8.8");
    }

    private void hqlExperiments(long id) {
        List<Student> all = students.findAll();
        System.out.println("HQL SELECT: " + all);
        if (!all.isEmpty()) {
            String email = all.getFirst().getEmail();
            System.out.println("HQL parameter: " + students.findByEmail(email));
        }
        System.out.println("HQL paging (max 10): count=" + students.findPage(0, 10).size());
        System.out.println("HQL projection: " + students.findAllEmails());
        System.out.println("HQL bulk UPDATE: updated=" + students.updateMarks(id, 9.5));
        System.out.println("PASS: select/parameter/paging/projection/mutation completed");
    }

    private void relationship() {
        Student student = new Student(uniqueEmail("reader"), "Minh", "Tran", 8.0);
        Book book = new Book("ISBN-" + UUID.randomUUID(), "Hibernate in Practice", "SBA301");
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(student);
                session.persist(book);
                student.addBook(book);
                transaction.commit();
            } catch (RuntimeException exception) {
                rollback(transaction);
                throw exception;
            }
        }
        System.out.printf("PASS: STUDENTS_BOOKS row for student_id=%d, book_id=%d%n",
                student.getId(), book.getId());
    }

    private void rollback() {
        String email = uniqueEmail("rollback");
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(new Student(email, "Atomic", "One", 5.0));
                session.flush(); // Prove SQL may execute before commit.
                throw new IllegalStateException("Deliberate failure after INSERT/flush");
            } catch (RuntimeException expected) {
                rollback(transaction);
                System.out.println("Expected failure: " + expected.getMessage());
            }
        }
        long rows = students.countByEmail(email);
        System.out.println((rows == 0 ? "PASS" : "FAIL") + ": rows after rollback=" + rows);
    }

    private void breakHql() {
        try (Session session = sessionFactory.openSession()) {
            session.createQuery("from STUDENTS", Student.class).getResultList();
            System.out.println("FAIL: invalid HQL unexpectedly succeeded");
        } catch (RuntimeException expected) {
            System.out.println("PASS: expected HQL error: " + expected.getClass().getSimpleName());
            System.out.println("FIX: use 'from Student' because HQL uses entity/property names.");
        }
    }

    private void batch(int requestedSize) {
        int size = Math.max(1, requestedSize);
        int checkpoint = 50;
        String runId = UUID.randomUUID().toString().substring(0, 8);
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                for (int index = 1; index <= size; index++) {
                    session.persist(new Student(
                            "batch-" + runId + '-' + index + "@fu.edu.vn", "Batch", "Student", 5.0));
                    if (index % checkpoint == 0) {
                        session.flush();
                        session.clear();
                        System.out.println("checkpoint=" + index + ": flush + clear");
                    }
                }
                transaction.commit();
            } catch (RuntimeException exception) {
                rollback(transaction);
                throw exception;
            }
        }
        System.out.println("PASS: inserted=" + size + "; Session cleared every " + checkpoint);
    }

    private static Student requireStudent(Session session, long id) {
        Student student = session.get(Student.class, id);
        if (student == null) {
            throw new IllegalArgumentException(
                    "Student id=" + id + " not found. Run --lab.experiment=lifecycle first.");
        }
        return student;
    }

    private static String uniqueEmail(String prefix) {
        return prefix + '-' + UUID.randomUUID() + "@fu.edu.vn";
    }

    private static void rollback(Transaction transaction) {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }
    }
}
