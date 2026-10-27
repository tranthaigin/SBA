package fu.edu.sba301.assignment02.service;

import fu.edu.sba301.assignment02.dto.SystemAccountRequest;
import fu.edu.sba301.assignment02.entity.SystemAccount;
import fu.edu.sba301.assignment02.exception.BusinessRuleViolationException;
import fu.edu.sba301.assignment02.exception.ResourceNotFoundException;
import fu.edu.sba301.assignment02.repository.NewsArticleRepository;
import fu.edu.sba301.assignment02.repository.SystemAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SystemAccountService {

    private final SystemAccountRepository accountRepository;
    private final NewsArticleRepository newsArticleRepository;

    public SystemAccountService(SystemAccountRepository accountRepository, NewsArticleRepository newsArticleRepository) {
        this.accountRepository = accountRepository;
        this.newsArticleRepository = newsArticleRepository;
    }

    public List<SystemAccount> getAllAccounts(String search) {
        if (search != null && !search.isBlank()) {
            return accountRepository.findByAccountNameContainingIgnoreCaseOrAccountEmailContainingIgnoreCase(search.trim(), search.trim());
        }
        return accountRepository.findAll();
    }

    public SystemAccount getAccountById(Short id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));
    }

    public SystemAccount createAccount(SystemAccountRequest req) {
        if (accountRepository.findByAccountEmail(req.getAccountEmail().trim()).isPresent()) {
            throw new BusinessRuleViolationException("Email already exists: " + req.getAccountEmail());
        }

        Short id = req.getAccountId();
        if (id == null) {
            id = (short) (accountRepository.findAll().stream()
                    .mapToInt(SystemAccount::getAccountId)
                    .max()
                    .orElse(0) + 1);
        }

        SystemAccount acc = new SystemAccount();
        acc.setAccountId(id);
        acc.setAccountName(req.getAccountName().trim());
        acc.setAccountEmail(req.getAccountEmail().trim());
        acc.setAccountRole(req.getAccountRole());
        acc.setAccountPassword(req.getAccountPassword());
        return accountRepository.save(acc);
    }

    public SystemAccount updateAccount(Short id, SystemAccountRequest req) {
        SystemAccount acc = getAccountById(id);
        accountRepository.findByAccountEmail(req.getAccountEmail().trim())
                .filter(other -> !other.getAccountId().equals(id))
                .ifPresent(other -> {
                    throw new BusinessRuleViolationException("Email already used by another account");
                });

        acc.setAccountName(req.getAccountName().trim());
        acc.setAccountEmail(req.getAccountEmail().trim());
        acc.setAccountRole(req.getAccountRole());
        if (req.getAccountPassword() != null && !req.getAccountPassword().isBlank()) {
            acc.setAccountPassword(req.getAccountPassword());
        }
        return accountRepository.save(acc);
    }

    public void deleteAccount(Short id) {
        SystemAccount acc = getAccountById(id);
        if (newsArticleRepository.existsByCreatedById(id)) {
            throw new BusinessRuleViolationException("Cannot delete Account because it is the creator of existing news articles");
        }
        accountRepository.delete(acc);
    }
}
