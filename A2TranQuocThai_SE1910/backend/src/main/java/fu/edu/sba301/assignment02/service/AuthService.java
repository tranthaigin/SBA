package fu.edu.sba301.assignment02.service;

import fu.edu.sba301.assignment02.dto.LoginRequest;
import fu.edu.sba301.assignment02.dto.LoginResponse;
import fu.edu.sba301.assignment02.entity.SystemAccount;
import fu.edu.sba301.assignment02.exception.BusinessRuleViolationException;
import fu.edu.sba301.assignment02.repository.SystemAccountRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final SystemAccountRepository accountRepository;

    public AuthService(SystemAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public LoginResponse login(LoginRequest req) {
        SystemAccount acc = accountRepository.findByAccountEmail(req.getEmail().trim())
                .orElseThrow(() -> new BusinessRuleViolationException("Invalid email or password"));

        if (!acc.getAccountPassword().equals(req.getPassword())) {
            throw new BusinessRuleViolationException("Invalid email or password");
        }

        return new LoginResponse(
                acc.getAccountId(),
                acc.getAccountName(),
                acc.getAccountEmail(),
                acc.getAccountRole()
        );
    }
}
