package fu.edu.sba301.assignment03.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class BookingCreateRequest {

    private Integer customerId; // Optional if derived from authenticated user

    @NotEmpty(message = "At least one room must be included in booking")
    private List<BookingItem> items;

    public static class BookingItem {
        @NotNull(message = "Room ID is required")
        private Integer roomId;

        @NotNull(message = "Start date is required")
        private LocalDate startDate;

        @NotNull(message = "End date is required")
        private LocalDate endDate;

        private BigDecimal actualPrice;

        public BookingItem() {
        }

        public Integer getRoomId() {
            return roomId;
        }

        public void setRoomId(Integer roomId) {
            this.roomId = roomId;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }

        public BigDecimal getActualPrice() {
            return actualPrice;
        }

        public void setActualPrice(BigDecimal actualPrice) {
            this.actualPrice = actualPrice;
        }
    }

    public BookingCreateRequest() {
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public List<BookingItem> getItems() {
        return items;
    }

    public void setItems(List<BookingItem> items) {
        this.items = items;
    }
}
