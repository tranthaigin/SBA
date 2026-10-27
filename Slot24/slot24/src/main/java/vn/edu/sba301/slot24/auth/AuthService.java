package vn.edu.sba301.slot24.auth;

import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.sba301.slot24.security.JwtService;
import vn.edu.sba301.slot24.user.AppUser;
import vn.edu.sba301.slot24.user.AppUserRepository;

import java.util.Locale;

@Service
public class AuthService {

    private final AppUserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            AppUserRepository users,
            PasswordEncoder encoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.users = users;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        String email = normalizeEmail(request.email());
        if (users.existsByEmail(email)) {
            throw new EmailAlreadyExistsException();
        }

        AppUser user = new AppUser();
        user.setEmail(email);
        user.setPasswordHash(encoder.encode(request.password()));
        user.setRole("USER");
        user.setEnabled(true);
        AppUser saved = users.save(user);
        return new SignupResponse(saved.getId(), saved.getEmail(), saved.getRole());
    }

    public LoginResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password())
        );
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        return new LoginResponse(jwtService.generateToken(principal), "Bearer");
    }

    private String normalizeEmail(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }
}
