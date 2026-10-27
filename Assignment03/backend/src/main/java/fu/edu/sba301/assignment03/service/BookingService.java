package fu.edu.sba301.assignment03.service;

import fu.edu.sba301.assignment03.dto.BookingCreateRequest;
import fu.edu.sba301.assignment03.entity.*;
import fu.edu.sba301.assignment03.repository.BookingDetailRepository;
import fu.edu.sba301.assignment03.repository.BookingReservationRepository;
import fu.edu.sba301.assignment03.repository.CustomerRepository;
import fu.edu.sba301.assignment03.repository.RoomInformationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class BookingService {

    private final BookingReservationRepository bookingRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final CustomerRepository customerRepository;
    private final RoomInformationRepository roomRepository;

    public BookingService(BookingReservationRepository bookingRepository,
                          BookingDetailRepository bookingDetailRepository,
                          CustomerRepository customerRepository,
                          RoomInformationRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.bookingDetailRepository = bookingDetailRepository;
        this.customerRepository = customerRepository;
        this.roomRepository = roomRepository;
    }

    public List<BookingReservation> getCustomerBookings(String customerEmail) {
        Customer customer = customerRepository.findByEmailAddress(customerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerEmail));
        return bookingRepository.findByCustomerOrderByBookingDateDesc(customer);
    }

    public List<BookingReservation> getAllBookings() {
        return bookingRepository.findAll();
    }

    public BookingReservation createBooking(String customerEmail, BookingCreateRequest req) {
        Customer customer;
        if (customerEmail != null) {
            customer = customerRepository.findByEmailAddress(customerEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerEmail));
        } else if (req.getCustomerId() != null) {
            customer = customerRepository.findById(req.getCustomerId())
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + req.getCustomerId()));
        } else {
            throw new IllegalArgumentException("Customer information is required");
        }

        BookingReservation reservation = new BookingReservation();
        reservation.setCustomer(customer);
        reservation.setBookingDate(LocalDate.now());
        reservation.setBookingStatus((byte) 1); // Confirmed
        reservation.setTotalPrice(BigDecimal.ZERO);
        reservation = bookingRepository.saveAndFlush(reservation);

        BigDecimal total = BigDecimal.ZERO;

        for (BookingCreateRequest.BookingItem item : req.getItems()) {
            RoomInformation room = roomRepository.findById(item.getRoomId())
                    .orElseThrow(() -> new IllegalArgumentException("Room not found: " + item.getRoomId()));

            long days = ChronoUnit.DAYS.between(item.getStartDate(), item.getEndDate());
            if (days <= 0) days = 1;

            BigDecimal pricePerDay = item.getActualPrice() != null ? item.getActualPrice() : room.getRoomPricePerDay();
            if (pricePerDay == null) pricePerDay = BigDecimal.valueOf(50);
            BigDecimal itemTotal = pricePerDay.multiply(BigDecimal.valueOf(days));

            BookingDetail detail = new BookingDetail();
            detail.setBookingReservationId(reservation.getBookingReservationId());
            detail.setRoomId(room.getRoomId());
            detail.setBookingReservation(reservation);
            detail.setRoomInformation(room);
            detail.setStartDate(item.getStartDate());
            detail.setEndDate(item.getEndDate());
            detail.setActualPrice(pricePerDay);

            bookingDetailRepository.save(detail);
            reservation.getBookingDetails().add(detail);
            total = total.add(itemTotal);
        }

        reservation.setTotalPrice(total);
        return bookingRepository.save(reservation);
    }

    public BookingReservation updateStatus(Integer bookingId, Byte status) {
        BookingReservation b = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
        b.setBookingStatus(status);
        return bookingRepository.save(b);
    }
}
