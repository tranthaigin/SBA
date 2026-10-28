package fu.edu.sba301.assignment02.controller;

import fu.edu.sba301.assignment02.dto.SystemAccountRequest;
import fu.edu.sba301.assignment02.entity.SystemAccount;
import fu.edu.sba301.assignment02.service.SystemAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class SystemAccountController {

    private final SystemAccountService accountService;

    public SystemAccountController(SystemAccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<List<SystemAccount>> getAll(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(accountService.getAllAccounts(search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SystemAccount> getById(@PathVariable Short id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @PostMapping
    public ResponseEntity<SystemAccount> create(@Valid @RequestBody SystemAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SystemAccount> update(@PathVariable Short id, @Valid @RequestBody SystemAccountRequest request) {
        return ResponseEntity.ok(accountService.updateAccount(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Short id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
