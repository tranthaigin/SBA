package fu.edu.sba301.slot28.repository;

import fu.edu.sba301.slot28.entity.Student;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class StudentRepository {
    private final SessionFactory sessionFactory;

    public StudentRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Student save(Student student) {
        return inTransaction(session -> {
            session.persist(student);
            return student;
        });
    }

    public Optional<Student> findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(Student.class, id));
        }
    }

    public List<Student> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Student s order by s.lastName, s.id", Student.class)
                    .getResultList();
        }
    }

    public List<Student> findByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Student s where s.email = :email", Student.class)
                    .setParameter("email", email)
                    .getResultList();
        }
    }

    public List<Student> findPage(int offset, int pageSize) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Student s order by s.id", Student.class)
                    .setFirstResult(offset)
                    .setMaxResults(pageSize)
                    .getResultList();
        }
    }

    public List<String> findAllEmails() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "select s.email from Student s order by s.email", String.class)
                    .getResultList();
        }
    }

    public int updateMarks(long id, double marks) {
        return inTransaction(session -> session.createMutationQuery(
                        "update Student s set s.marks = :marks where s.id = :id")
                .setParameter("marks", marks)
                .setParameter("id", id)
                .executeUpdate());
    }

    public long countByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "select count(s) from Student s where s.email = :email", Long.class)
                    .setParameter("email", email)
                    .getSingleResult();
        }
    }

    private <T> T inTransaction(SessionWork<T> work) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            T result = work.execute(session);
            transaction.commit();
            return result;
        } catch (RuntimeException exception) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw exception;
        }
    }

    @FunctionalInterface
    private interface SessionWork<T> {
        T execute(Session session);
    }
}
