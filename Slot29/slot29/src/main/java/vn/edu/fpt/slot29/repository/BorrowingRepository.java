package vn.edu.fpt.slot29.repository;

import org.springframework.stereotype.Repository;
import vn.edu.fpt.slot29.dao.BorrowingDAO;
import vn.edu.fpt.slot29.domain.Borrowing;
import java.util.Optional;

@Repository
public class BorrowingRepository implements IBorrowingRepository {
    private final BorrowingDAO dao;
    public BorrowingRepository(BorrowingDAO dao) { this.dao = dao; }
    @Override public Borrowing save(Borrowing borrowing) { return dao.save(borrowing); }
    @Override public Optional<Borrowing> findActive(Long studentId, Long bookId) {
        return dao.findActive(studentId, bookId);
    }
}
