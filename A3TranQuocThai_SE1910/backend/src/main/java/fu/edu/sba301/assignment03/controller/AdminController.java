package fu.edu.sba301.assignment03.controller;

import fu.edu.sba301.assignment03.dto.CustomerRequest;
import fu.edu.sba301.assignment03.dto.RoomRequest;
import fu.edu.sba301.assignment03.entity.BookingReservation;
import fu.edu.sba301.assignment03.entity.Customer;
import fu.edu.sba301.assignment03.entity.RoomInformation;
import fu.edu.sba301.assignment03.entity.RoomType;
import fu.edu.sba301.assignment03.service.BookingService;
import fu.edu.sba301.assignment03.service.CustomerService;
import fu.edu.sba301.assignment03.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final CustomerService customerService;
    private final RoomService roomService;
    private final BookingService bookingService;

    public AdminController(CustomerService customerService,
                           RoomService roomService,
                           BookingService bookingService) {
        this.customerService = customerService;
        this.roomService = roomService;
        this.bookingService = bookingService;
    }

    // --- Customers Management ---
    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getAllCustomers(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(customerService.getAllCustomers(search));
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Integer id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PostMapping("/customers")
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody CustomerRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(req));
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Integer id, @Valid @RequestBody CustomerRequest req) {
        return ResponseEntity.ok(customerService.updateCustomer(id, req));
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Integer id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    // --- Rooms Management ---
    @GetMapping("/rooms")
    public ResponseEntity<List<RoomInformation>> getAllRooms(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(roomService.getAllRooms(search));
    }

    @GetMapping("/room-types")
    public ResponseEntity<List<RoomType>> getAllRoomTypes() {
        return ResponseEntity.ok(roomService.getAllRoomTypes());
    }

    @PostMapping("/rooms")
    public ResponseEntity<RoomInformation> createRoom(@Valid @RequestBody RoomRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(req));
    }

    @PutMapping("/rooms/{id}")
    public ResponseEntity<RoomInformation> updateRoom(@PathVariable Integer id, @Valid @RequestBody RoomRequest req) {
        return ResponseEntity.ok(roomService.updateRoom(id, req));
    }

    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<Map<String, Object>> deleteRoom(@PathVariable Integer id) {
        boolean hardDeleted = roomService.deleteRoom(id);
        return ResponseEntity.ok(Map.of(
                "hardDeleted", hardDeleted,
                "message", hardDeleted
                        ? "Room permanently deleted"
                        : "Room has past booking history, status updated to inactive"
        ));
    }

    // --- Booking Reservations Management ---
    @PostMapping("/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public Object createBooking(@Valid @RequestBody fu.edu.sba301.assignment03.dto.BookingCreateRequest req) { return bookingService.createBooking(null,req); }
    @PutMapping("/bookings/{id}")
    public Object updateBooking(@PathVariable Integer id,@Valid @RequestBody fu.edu.sba301.assignment03.dto.BookingCreateRequest req) { return bookingService.updateBooking(id,req); }
    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Integer id) { bookingService.deleteBooking(id); return ResponseEntity.noContent().build(); }
    @GetMapping("/bookings")
    public ResponseEntity<List<BookingReservation>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PutMapping("/bookings/{id}/status")
    public ResponseEntity<BookingReservation> updateBookingStatus(@PathVariable Integer id, @RequestParam Byte status) {
        return ResponseEntity.ok(bookingService.updateStatus(id, status));
    }
}
