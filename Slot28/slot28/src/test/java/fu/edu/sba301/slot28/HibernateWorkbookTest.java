package fu.edu.sba301.slot28;

import fu.edu.sba301.slot28.entity.Book;
import fu.edu.sba301.slot28.entity.Student;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HibernateWorkbookTest {
    private static SessionFactory sessionFactory;
    private static Long aliceId;

    @Test
    @Order(0)
    void courseStyleHibernateXmlCanBeParsed() {
        Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
        assertEquals("com.microsoft.sqlserver.jdbc.SQLServerDriver",
                configuration.getProperty("hibernate.connection.driver_class"));
        assertTrue(configuration.getProperty("hibernate.connection.url").contains("LibraryDB"));
        assertEquals("update", configuration.getProperty("hibernate.hbm2ddl.auto"));
    }

    @BeforeAll
    static void buildSessionFactory() {
        sessionFactory = new Configuration()
                .addAnnotatedClass(Student.class)
                .addAnnotatedClass(Book.class)
                .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
                .setProperty("hibernate.connection.url",
                        "jdbc:h2:mem:library;MODE=MSSQLServer;DB_CLOSE_DELAY=-1")
                .setProperty("hibernate.connection.username", "sa")
                .setProperty("hibernate.connection.password", "")
                .setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .setProperty("hibernate.show_sql", "false")
                .buildSessionFactory();
    }

    @AfterAll
    static void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    @Order(1)
    void persistMovesEntityFromTransientToManagedThenDetached() {
        Student alice = new Student("alice@fu.edu.vn", "Alice", "Nguyen", 8.5);

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(alice);
            assertTrue(session.contains(alice));
            transaction.commit();
        }

        aliceId = alice.getId();
        assertNotNull(aliceId);
        try (Session session = sessionFactory.openSession()) {
            assertFalse(session.contains(alice));
            assertNotNull(session.get(Student.class, aliceId));
        }
    }

    @Test
    @Order(2)
    void dirtyCheckingUpdatesManagedEntityWithoutExplicitUpdate() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.get(Student.class, aliceId).setMarks(9.0);
            transaction.commit();
        }

        try (Session session = sessionFactory.openSession()) {
            assertEquals(9.0, session.get(Student.class, aliceId).getMarks());
        }
    }

    @Test
    @Order(3)
    void firstLevelCacheReturnsSameObjectButDifferentSessionDoesNot() {
        Student first;
        try (Session session = sessionFactory.openSession()) {
            first = session.get(Student.class, aliceId);
            assertSame(first, session.get(Student.class, aliceId));
        }
        try (Session session = sessionFactory.openSession()) {
            assertNotSame(first, session.get(Student.class, aliceId));
        }
    }

    @Test
    @Order(4)
    void detachedChangeIsIgnoredUntilMerge() {
        Student detached;
        try (Session session = sessionFactory.openSession()) {
            detached = session.get(Student.class, aliceId);
        }
        detached.setMarks(7.0);

        try (Session session = sessionFactory.openSession()) {
            assertEquals(9.0, session.get(Student.class, aliceId).getMarks());
            Transaction transaction = session.beginTransaction();
            Student managed = session.merge(detached);
            assertNotSame(detached, managed);
            assertFalse(session.contains(detached));
            assertTrue(session.contains(managed));
            managed.setMarks(8.8);
            transaction.commit();
        }

        try (Session session = sessionFactory.openSession()) {
            assertEquals(8.8, session.get(Student.class, aliceId).getMarks());
        }
    }

    @Test
    @Order(5)
    void hqlAndManyToManyMappingWork() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Student student = session.get(Student.class, aliceId);
            Book book = new Book("978-0134685991", "Effective Java", "Joshua Bloch");
            session.persist(book);
            student.addBook(book);
            transaction.commit();
        }

        try (Session session = sessionFactory.openSession()) {
            Student result = session.createQuery(
                            "from Student s where s.email = :email", Student.class)
                    .setParameter("email", "alice@fu.edu.vn")
                    .getSingleResult();
            assertEquals(1, result.getBooks().size());
            assertEquals(1, session.createQuery(
                            "select s.email from Student s order by s.email", String.class)
                    .setMaxResults(10)
                    .getResultList().size());
        }
    }

    @Test
    @Order(6)
    void rollbackLeavesNoPartialRow() {
        String email = "rollback@fu.edu.vn";
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            assertThrows(IllegalStateException.class, () -> {
                session.persist(new Student(email, "Rollback", "Demo", 5.0));
                session.flush();
                throw new IllegalStateException("deliberate");
            });
            transaction.rollback();
        }

        try (Session session = sessionFactory.openSession()) {
            long count = session.createQuery(
                            "select count(s) from Student s where s.email = :email", Long.class)
                    .setParameter("email", email)
                    .getSingleResult();
            assertEquals(0, count);
        }
    }
}
