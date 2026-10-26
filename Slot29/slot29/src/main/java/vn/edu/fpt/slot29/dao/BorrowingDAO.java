package vn.edu.fpt.slot29.dao;

import jakarta.persistence.*;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.slot29.domain.*;
import java.util.Optional;

@Repository
public class BorrowingDAO {
    @PersistenceContext private EntityManager entityManager;
    private Session session() { return entityManager.unwrap(Session.class); }

    public Borrowing save(Borrowing borrowing) {
        session().persist(borrowing);
        return borrowing;
    }

    public Optional<Borrowing> findActive(Long studentId, Long bookId) {
        return session().createQuery("""
                select br from Borrowing br join fetch br.student join fetch br.book
                where br.student.id=:studentId and br.book.id=:bookId
                and br.status in (:borrowed, :overdue)
                """, Borrowing.class)
                .setParameter("studentId", studentId).setParameter("bookId", bookId)
                .setParameter("borrowed", BorrowingStatus.BORROWED)
                .setParameter("overdue", BorrowingStatus.OVERDUE)
                .uniqueResultOptional();
    }
}
