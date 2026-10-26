package vn.edu.fpt.slot29.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.slot29.domain.Book;

import java.util.List;
import java.util.Optional;

@Repository
public class BookDAO {
    @PersistenceContext
    private EntityManager entityManager;

    private Session session() { return entityManager.unwrap(Session.class); }

    public Book save(Book book) {
        session().persist(book);
        return book;
    }

    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(session().get(Book.class, id));
    }

    public List<Book> findAll() {
        return session().createQuery("from Book b order by b.id", Book.class).getResultList();
    }

    public long count() {
        return session().createQuery("select count(b) from Book b", Long.class).getSingleResult();
    }
}
