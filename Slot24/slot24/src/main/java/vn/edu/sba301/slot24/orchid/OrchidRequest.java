package vn.edu.sba301.slot24.orchid;

import jakarta.validation.constraints.NotBlank;

public record OrchidRequest(@NotBlank String name, @NotBlank String description) {
}
