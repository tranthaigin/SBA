package vn.edu.fpt.slot29.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.slot29.domain.Book;
import vn.edu.fpt.slot29.domain.Student;

import java.util.*;

@Repository
public class StudentDAO {
    @PersistenceContext
    private EntityManager entityManager;

    private Session session() { return entityManager.unwrap(Session.class); }

    public Student save(Student student) {
        session().persist(student);
        return student;
    }

    public List<Student> findAll() {
        return session().createQuery("from Student s order by s.id", Student.class).getResultList();
    }

    public Optional<Student> findById(Long id) {
        return Optional.ofNullable(session().get(Student.class, id));
    }

    public Optional<Student> findByIdWithBooks(Long id) {
        return session().createQuery("""
                select distinct s from Student s
                left join fetch s.books
                where s.id = :id
                """, Student.class).setParameter("id", id).uniqueResultOptional();
    }

    public Student update(Student detached) {
        return session().merge(detached);
    }

    public boolean delete(Long id) {
        Student student = session().get(Student.class, id);
        if (student == null) return false;
        for (Book book : new HashSet<>(student.getBooks())) student.removeBook(book);
        session().remove(student);
        return true;
    }

    public List<Student> findStudentsByBook(Long bookId) {
        return session().createQuery("""
                select distinct s from Student s
                join s.books b
                where b.id = :bookId
                order by s.id
                """, Student.class).setParameter("bookId", bookId).getResultList();
    }
}
