package fu.edu.sba301.assignment03.service;

import fu.edu.sba301.assignment03.dto.CustomerRequest;
import fu.edu.sba301.assignment03.entity.Customer;
import fu.edu.sba301.assignment03.repository.BookingReservationRepository;
import fu.edu.sba301.assignment03.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final BookingReservationRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository,
                           BookingReservationRepository bookingRepository,
                           PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.bookingRepository = bookingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Customer> getAllCustomers(String search) {
        if (search != null && !search.isBlank()) {
            return customerRepository.findByCustomerFullNameContainingIgnoreCaseOrEmailAddressContainingIgnoreCase(search.trim(), search.trim());
        }
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Integer id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + id));
    }

    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmailAddress(email)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with email: " + email));
    }

    public Customer createCustomer(CustomerRequest req) {
        if (customerRepository.findByEmailAddress(req.getEmailAddress().trim()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + req.getEmailAddress());
        }

        Customer c = new Customer();
        c.setCustomerFullName(req.getCustomerFullName().trim());
        c.setTelephone(req.getTelephone());
        c.setEmailAddress(req.getEmailAddress().trim());
        c.setCustomerBirthday(req.getCustomerBirthday());
        c.setCustomerStatus(req.getCustomerStatus() != null ? req.getCustomerStatus() : 1);
        String pass = req.getPassword() != null && !req.getPassword().isBlank() ? req.getPassword() : "123456";
        c.setPassword(passwordEncoder.encode(pass));
        return customerRepository.save(c);
    }

    public Customer updateCustomer(Integer id, CustomerRequest req) {
        Customer c = getCustomerById(id);
        customerRepository.findByEmailAddress(req.getEmailAddress().trim())
                .filter(other -> !other.getCustomerId().equals(id))
                .ifPresent(other -> {
                    throw new IllegalArgumentException("Email already taken by another customer");
                });

        c.setCustomerFullName(req.getCustomerFullName().trim());
        c.setTelephone(req.getTelephone());
        c.setEmailAddress(req.getEmailAddress().trim());
        c.setCustomerBirthday(req.getCustomerBirthday());
        if (req.getCustomerStatus() != null) {
            c.setCustomerStatus(req.getCustomerStatus());
        }
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            c.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        return customerRepository.save(c);
    }

    public void deleteCustomer(Integer id) {
        Customer c = getCustomerById(id);
        if (bookingRepository.existsByCustomer(c)) {
            // Cannot hard delete if associated with bookings -> deactivate
            c.setCustomerStatus((byte) 2);
            customerRepository.save(c);
        } else {
            customerRepository.delete(c);
        }
    }
}
