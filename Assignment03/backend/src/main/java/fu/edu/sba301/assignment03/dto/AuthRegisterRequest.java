package fu.edu.sba301.assignment03.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class AuthRegisterRequest {
    @NotBlank(message = "Full name is required")
    @Size(max = 50, message = "Full name maximum 50 characters")
    private String customerFullName;

    @Size(max = 12, message = "Telephone maximum 12 characters")
    private String telephone;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 50, message = "Email maximum 50 characters")
    private String emailAddress;

    private LocalDate customerBirthday;

    @NotBlank(message = "Password is required")
    @Size(min = 4, max = 50, message = "Password must be 4 to 50 characters")
    private String password;

    public AuthRegisterRequest() {
    }

    public String getCustomerFullName() {
        return customerFullName;
    }

    public void setCustomerFullName(String customerFullName) {
        this.customerFullName = customerFullName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public LocalDate getCustomerBirthday() {
        return customerBirthday;
    }

    public void setCustomerBirthday(LocalDate customerBirthday) {
        this.customerBirthday = customerBirthday;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
