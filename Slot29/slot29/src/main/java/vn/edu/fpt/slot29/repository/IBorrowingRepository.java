package vn.edu.fpt.slot29.repository;

import vn.edu.fpt.slot29.domain.Borrowing;
import java.util.Optional;

public interface IBorrowingRepository {
    Borrowing save(Borrowing borrowing);
    Optional<Borrowing> findActive(Long studentId, Long bookId);
}
