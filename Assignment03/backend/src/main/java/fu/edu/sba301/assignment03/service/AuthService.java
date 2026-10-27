package fu.edu.sba301.assignment03.service;

import fu.edu.sba301.assignment03.dto.AuthLoginRequest;
import fu.edu.sba301.assignment03.dto.AuthRegisterRequest;
import fu.edu.sba301.assignment03.dto.AuthResponse;
import fu.edu.sba301.assignment03.entity.Customer;
import fu.edu.sba301.assignment03.repository.CustomerRepository;
import fu.edu.sba301.assignment03.security.JwtTokenProvider;
import fu.edu.sba301.assignment03.security.StaffProperties;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final StaffProperties staffProperties;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(StaffProperties staffProperties,
                       CustomerRepository customerRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider) {
        this.staffProperties = staffProperties;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public AuthResponse login(AuthLoginRequest request) {
        // 1. Check if matches Staff from application.properties
        if (staffProperties.getEmail() != null &&
                staffProperties.getEmail().equalsIgnoreCase(request.getEmail().trim())) {
            if (staffProperties.getPassword().equals(request.getPassword())) {
                String token = tokenProvider.generateToken(
                        staffProperties.getEmail(),
                        staffProperties.getRole(),
                        staffProperties.getName(),
                        0
                );
                return new AuthResponse(token, staffProperties.getEmail(), staffProperties.getName(), staffProperties.getRole(), 0);
            } else {
                throw new BadCredentialsException("Invalid password");
            }
        }

        // 2. Check Customer repository
        Customer customer = customerRepository.findByEmailAddress(request.getEmail().trim())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        boolean matches = passwordEncoder.matches(request.getPassword(), customer.getPassword())
                || customer.getPassword().equals(request.getPassword()); // Support plain during migration

        if (!matches) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String role = "ROLE_CUSTOMER";
        String token = tokenProvider.generateToken(
                customer.getEmailAddress(),
                role,
                customer.getCustomerFullName(),
                customer.getCustomerId()
        );

        return new AuthResponse(token, customer.getEmailAddress(), customer.getCustomerFullName(), role, customer.getCustomerId());
    }

    public AuthResponse register(AuthRegisterRequest request) {
        if (customerRepository.findByEmailAddress(request.getEmailAddress().trim()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmailAddress());
        }

        Customer customer = new Customer();
        customer.setCustomerFullName(request.getCustomerFullName().trim());
        customer.setTelephone(request.getTelephone());
        customer.setEmailAddress(request.getEmailAddress().trim());
        customer.setCustomerBirthday(request.getCustomerBirthday());
        customer.setCustomerStatus((byte) 1);
        customer.setPassword(passwordEncoder.encode(request.getPassword()));

        Customer saved = customerRepository.save(customer);

        String role = "ROLE_CUSTOMER";
        String token = tokenProvider.generateToken(
                saved.getEmailAddress(),
                role,
                saved.getCustomerFullName(),
                saved.getCustomerId()
        );

        return new AuthResponse(token, saved.getEmailAddress(), saved.getCustomerFullName(), role, saved.getCustomerId());
    }
}
