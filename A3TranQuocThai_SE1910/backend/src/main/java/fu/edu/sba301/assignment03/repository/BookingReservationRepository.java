package fu.edu.sba301.assignment03.repository;

import fu.edu.sba301.assignment03.entity.BookingReservation;
import fu.edu.sba301.assignment03.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingReservationRepository extends JpaRepository<BookingReservation, Integer> {
    List<BookingReservation> findByCustomerOrderByBookingDateDesc(Customer customer);
    boolean existsByCustomer(Customer customer);
}
