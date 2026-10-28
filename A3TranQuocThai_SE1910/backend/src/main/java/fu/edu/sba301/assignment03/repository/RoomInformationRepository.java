package fu.edu.sba301.assignment03.repository;

import fu.edu.sba301.assignment03.entity.RoomInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomInformationRepository extends JpaRepository<RoomInformation, Integer> {
    List<RoomInformation> findByRoomStatus(Byte status);
    List<RoomInformation> findByRoomNumberContainingIgnoreCaseOrRoomDetailDescriptionContainingIgnoreCase(String number, String desc);
}
