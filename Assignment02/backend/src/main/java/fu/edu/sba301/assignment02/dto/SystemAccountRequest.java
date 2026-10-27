package fu.edu.sba301.assignment02.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SystemAccountRequest {
    private Short accountId;

    @NotBlank(message = "Account name cannot be blank")
    @Size(max = 100, message = "Account name max 100 characters")
    private String accountName;

    @NotBlank(message = "Account email cannot be blank")
    @Email(message = "Invalid email format")
    @Size(max = 70, message = "Account email max 70 characters")
    private String accountEmail;

    @NotNull(message = "Account role is required (1: Admin, 2: Staff)")
    private Integer accountRole;

    @NotBlank(message = "Account password cannot be blank")
    @Size(max = 70, message = "Account password max 70 characters")
    private String accountPassword;

    public SystemAccountRequest() {
    }

    public Short getAccountId() {
        return accountId;
    }

    public void setAccountId(Short accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountEmail() {
        return accountEmail;
    }

    public void setAccountEmail(String accountEmail) {
        this.accountEmail = accountEmail;
    }

    public Integer getAccountRole() {
        return accountRole;
    }

    public void setAccountRole(Integer accountRole) {
        this.accountRole = accountRole;
    }

    public String getAccountPassword() {
        return accountPassword;
    }

    public void setAccountPassword(String accountPassword) {
        this.accountPassword = accountPassword;
    }
}
