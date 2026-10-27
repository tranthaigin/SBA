package fu.edu.sba301.assignment03.service;

import fu.edu.sba301.assignment03.dto.RoomRequest;
import fu.edu.sba301.assignment03.entity.RoomInformation;
import fu.edu.sba301.assignment03.entity.RoomType;
import fu.edu.sba301.assignment03.repository.BookingDetailRepository;
import fu.edu.sba301.assignment03.repository.RoomInformationRepository;
import fu.edu.sba301.assignment03.repository.RoomTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RoomService {

    private final RoomInformationRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final BookingDetailRepository bookingDetailRepository;

    public RoomService(RoomInformationRepository roomRepository,
                       RoomTypeRepository roomTypeRepository,
                       BookingDetailRepository bookingDetailRepository) {
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.bookingDetailRepository = bookingDetailRepository;
    }

    public List<RoomInformation> getActiveRooms() {
        return roomRepository.findByRoomStatus((byte) 1);
    }

    public List<RoomInformation> getAllRooms(String search) {
        if (search != null && !search.isBlank()) {
            return roomRepository.findByRoomNumberContainingIgnoreCaseOrRoomDetailDescriptionContainingIgnoreCase(search.trim(), search.trim());
        }
        return roomRepository.findAll();
    }

    public RoomInformation getRoomById(Integer id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + id));
    }

    public List<RoomType> getAllRoomTypes() {
        return roomTypeRepository.findAll();
    }

    public RoomInformation createRoom(RoomRequest req) {
        RoomType type = roomTypeRepository.findById(req.getRoomTypeId())
                .orElseThrow(() -> new IllegalArgumentException("RoomType not found with ID: " + req.getRoomTypeId()));

        RoomInformation room = new RoomInformation();
        room.setRoomNumber(req.getRoomNumber().trim());
        room.setRoomDetailDescription(req.getRoomDetailDescription());
        room.setRoomMaxCapacity(req.getRoomMaxCapacity());
        room.setRoomType(type);
        room.setRoomStatus(req.getRoomStatus() != null ? req.getRoomStatus() : 1);
        room.setRoomPricePerDay(req.getRoomPricePerDay());
        return roomRepository.save(room);
    }

    public RoomInformation updateRoom(Integer id, RoomRequest req) {
        RoomInformation room = getRoomById(id);

        if (req.getRoomTypeId() != null) {
            RoomType type = roomTypeRepository.findById(req.getRoomTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("RoomType not found with ID: " + req.getRoomTypeId()));
            room.setRoomType(type);
        }

        if (req.getRoomNumber() != null) room.setRoomNumber(req.getRoomNumber().trim());
        if (req.getRoomDetailDescription() != null) room.setRoomDetailDescription(req.getRoomDetailDescription());
        if (req.getRoomMaxCapacity() != null) room.setRoomMaxCapacity(req.getRoomMaxCapacity());
        if (req.getRoomStatus() != null) room.setRoomStatus(req.getRoomStatus());
        if (req.getRoomPricePerDay() != null) room.setRoomPricePerDay(req.getRoomPricePerDay());

        return roomRepository.save(room);
    }

    /**
     * Requirement:
     * "The delete action will delete room information in the case this information is not belong to any renting transaction.
     * If the room information is already stored in a booking detail, just change the status."
     */
    public boolean deleteRoom(Integer id) {
        RoomInformation room = getRoomById(id);
        if (bookingDetailRepository.existsByRoomInformation(room)) {
            // Already stored in booking detail -> soft delete by changing status
            room.setRoomStatus((byte) 2);
            roomRepository.save(room);
            return false; // Soft deleted
        } else {
            // Hard delete
            roomRepository.delete(room);
            return true; // Hard deleted
        }
    }
}
