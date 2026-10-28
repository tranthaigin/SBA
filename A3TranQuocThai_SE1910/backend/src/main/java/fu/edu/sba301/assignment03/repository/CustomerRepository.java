package fu.edu.sba301.assignment03.repository;

import fu.edu.sba301.assignment03.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByEmailAddress(String email);
    List<Customer> findByCustomerFullNameContainingIgnoreCaseOrEmailAddressContainingIgnoreCase(String name, String email);
}
