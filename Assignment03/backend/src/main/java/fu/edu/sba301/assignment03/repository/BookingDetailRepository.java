package fu.edu.sba301.assignment03.repository;

import fu.edu.sba301.assignment03.entity.BookingDetail;
import fu.edu.sba301.assignment03.entity.BookingDetailId;
import fu.edu.sba301.assignment03.entity.RoomInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingDetailRepository extends JpaRepository<BookingDetail, BookingDetailId> {
    boolean existsByRoomInformation(RoomInformation roomInformation);
}
