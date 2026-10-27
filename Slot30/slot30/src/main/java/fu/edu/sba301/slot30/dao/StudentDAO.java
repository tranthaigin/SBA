package fu.edu.sba301.slot30.dao;

import fu.edu.sba301.slot30.entity.Student;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class StudentDAO {
    private final SessionFactory sessionFactory;

    public StudentDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Student save(Student student) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.persist(student);
                tx.commit();
                return student;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                throw ex;
            }
        }
    }

    public List<Student> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Student s order by s.id", Student.class).getResultList();
        }
    }

    public Optional<Student> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(Student.class, id));
        }
    }

    public Optional<Student> findByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Student s where s.email = :email", Student.class)
                    .setParameter("email", email)
                    .uniqueResultOptional();
        }
    }

    public Student updateMarks(Long id, double marks) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Student s = session.get(Student.class, id);
                if (s == null) {
                    tx.rollback();
                    throw new IllegalArgumentException("Student not found: " + id);
                }
                s.setMarks(marks); // dirty checking
                tx.commit();
                return s;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                throw ex;
            }
        }
    }

    public Student update(Student detached) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Student managed = session.merge(detached);
                tx.commit();
                return managed;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                throw ex;
            }
        }
    }

    public boolean delete(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Student s = session.get(Student.class, id);
                if (s == null) {
                    tx.rollback();
                    return false;
                }
                session.remove(s);
                tx.commit();
                return true;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                throw ex;
            }
        }
    }
}
