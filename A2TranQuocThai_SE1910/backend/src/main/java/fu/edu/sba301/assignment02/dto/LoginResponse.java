package fu.edu.sba301.assignment02.dto;

public class LoginResponse {
    private Short accountId;
    private String accountName;
    private String accountEmail;
    private Integer accountRole; // 1 = Admin, 2 = Staff

    public LoginResponse() {
    }

    public LoginResponse(Short accountId, String accountName, String accountEmail, Integer accountRole) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.accountEmail = accountEmail;
        this.accountRole = accountRole;
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
}
