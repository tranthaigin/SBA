package vn.edu.fpt.slot29.dao.manual;

import org.hibernate.*;
import vn.edu.fpt.slot29.domain.Student;
import java.util.*;
import java.util.function.Function;

/** Slot-28 style DAO. Each method owns a Session/Transaction, so it is not ideal for multi-step use cases. */
public class ManualStudentDAO {
    private final SessionFactory sessionFactory;
    public ManualStudentDAO(SessionFactory sessionFactory) { this.sessionFactory = sessionFactory; }

    public Student save(Student student) { return write(s -> { s.persist(student); return student; }); }
    public Student update(Student detached) { return write(s -> s.merge(detached)); }
    public List<Student> findAll() {
        try (Session s = sessionFactory.openSession()) {
            return s.createQuery("from Student st order by st.id", Student.class).getResultList();
        }
    }
    public Optional<Student> findById(Long id) {
        try (Session s = sessionFactory.openSession()) { return Optional.ofNullable(s.get(Student.class, id)); }
    }
    public Optional<Student> findByIdWithBooks(Long id) {
        try (Session s = sessionFactory.openSession()) {
            return s.createQuery("select distinct st from Student st left join fetch st.books where st.id=:id", Student.class)
                    .setParameter("id", id).uniqueResultOptional();
        }
    }
    public boolean delete(Long id) {
        return write(s -> {
            Student student = s.get(Student.class, id);
            if (student == null) return false;
            s.remove(student);
            return true;
        });
    }
    private <T> T write(Function<Session, T> work) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                T result = work.apply(session);
                tx.commit();
                return result;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                throw ex;
            }
        }
    }
}
