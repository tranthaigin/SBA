package fu.edu.sba301.assignment03.controller;

import fu.edu.sba301.assignment03.dto.BookingCreateRequest;
import fu.edu.sba301.assignment03.dto.CustomerRequest;
import fu.edu.sba301.assignment03.entity.BookingReservation;
import fu.edu.sba301.assignment03.entity.Customer;
import fu.edu.sba301.assignment03.service.BookingService;
import fu.edu.sba301.assignment03.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/customer")
@CrossOrigin(origins = "*")
public class CustomerPortalController {

    private final CustomerService customerService;
    private final BookingService bookingService;

    public CustomerPortalController(CustomerService customerService, BookingService bookingService) {
        this.customerService = customerService;
        this.bookingService = bookingService;
    }

    @GetMapping("/profile")
    public ResponseEntity<Customer> getProfile(Principal principal) {
        return ResponseEntity.ok(customerService.getCustomerByEmail(principal.getName()));
    }

    @PutMapping("/profile")
    public ResponseEntity<Customer> updateProfile(Principal principal, @Valid @RequestBody CustomerRequest req) {
        Customer c = customerService.getCustomerByEmail(principal.getName());
        return ResponseEntity.ok(customerService.updateCustomer(c.getCustomerId(), req));
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingReservation>> getMyBookings(Principal principal) {
        return ResponseEntity.ok(bookingService.getCustomerBookings(principal.getName()));
    }

    @PostMapping("/bookings")
    public ResponseEntity<BookingReservation> createBooking(Principal principal,
                                                            @Valid @RequestBody BookingCreateRequest req) {
        BookingReservation res = bookingService.createBooking(principal.getName(), req);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
}
