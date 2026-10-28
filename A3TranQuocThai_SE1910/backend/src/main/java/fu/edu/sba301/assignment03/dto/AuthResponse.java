package fu.edu.sba301.assignment03.dto;

public class AuthResponse {
    private String token;
    private String email;
    private String fullName;
    private String role;
    private Integer customerId;

    public AuthResponse() {
    }

    public AuthResponse(String token, String email, String fullName, String role, Integer customerId) {
        this.token = token;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.customerId = customerId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
}
