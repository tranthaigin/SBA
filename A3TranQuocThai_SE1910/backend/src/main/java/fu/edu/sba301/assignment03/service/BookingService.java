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

    public void deleteBooking(Integer id) {
        var booking=bookingRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Booking not found"));
        bookingRepository.delete(booking);
    }
    public BookingReservation updateBooking(Integer id, BookingCreateRequest req) {
        var booking=bookingRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Booking not found"));
        if(req.getCustomerId()==null) throw new IllegalArgumentException("Customer is required");
        booking.setCustomer(customerRepository.findById(req.getCustomerId()).orElseThrow(()->new IllegalArgumentException("Customer not found")));
        booking.getBookingDetails().clear();
        bookingRepository.saveAndFlush(booking);
        fillDetails(booking,req,false);
        return bookingRepository.save(booking);
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

        fillDetails(reservation,req,customerEmail!=null);
        return bookingRepository.save(reservation);
    }
    private void fillDetails(BookingReservation reservation, BookingCreateRequest req, boolean online) {
        BigDecimal total=BigDecimal.ZERO;
        java.util.Set<Integer> rooms=new java.util.HashSet<>();
        for(var item:req.getItems()) {
            if(!rooms.add(item.getRoomId())) throw new IllegalArgumentException("A room may appear only once per booking");
            var room=roomRepository.findById(item.getRoomId()).orElseThrow(()->new IllegalArgumentException("Room not found"));
            if(room.getRoomStatus()!=1) throw new IllegalArgumentException("Room is inactive");
            long days=ChronoUnit.DAYS.between(item.getStartDate(),item.getEndDate());
            if(days<=0) throw new IllegalArgumentException("End date must be after start date");
            var price=online || item.getActualPrice()==null ? room.getRoomPricePerDay():item.getActualPrice();
            if(price==null || price.signum()<0) throw new IllegalArgumentException("Invalid room price");
            var detail=new BookingDetail();
            detail.setBookingReservationId(reservation.getBookingReservationId());
            detail.setRoomId(room.getRoomId()); detail.setBookingReservation(reservation); detail.setRoomInformation(room);
            detail.setStartDate(item.getStartDate());detail.setEndDate(item.getEndDate());detail.setActualPrice(price);
            reservation.getBookingDetails().add(detail);
            total=total.add(price.multiply(BigDecimal.valueOf(days)));
        }
        reservation.setTotalPrice(total);
    }

    public BookingReservation updateStatus(Integer bookingId, Byte status) {
        BookingReservation b = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
        if(status==null || status<1 || status>3) throw new IllegalArgumentException("Invalid booking status");
        b.setBookingStatus(status);
        return bookingRepository.save(b);
    }
}
