package fu.edu.sba301.assignment02.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "SystemAccount")
public class SystemAccount implements Serializable {

    @Id
    @Column(name = "AccountID")
    private Short accountId;

    @Column(name = "AccountName", length = 100)
    private String accountName;

    @Column(name = "AccountEmail", unique = true, length = 70)
    private String accountEmail;

    /**
     * Role: 1 = Admin, 2 = Staff
     */
    @Column(name = "AccountRole")
    private Integer accountRole;

    @Column(name = "AccountPassword", length = 70)
    private String accountPassword;

    public SystemAccount() {
    }

    public SystemAccount(Short accountId, String accountName, String accountEmail, Integer accountRole, String accountPassword) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.accountEmail = accountEmail;
        this.accountRole = accountRole;
        this.accountPassword = accountPassword;
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
