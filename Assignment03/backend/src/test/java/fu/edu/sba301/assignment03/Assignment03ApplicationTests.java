package fu.edu.sba301.assignment03;

import com.fasterxml.jackson.databind.ObjectMapper;
import fu.edu.sba301.assignment03.dto.AuthLoginRequest;
import fu.edu.sba301.assignment03.dto.AuthResponse;
import fu.edu.sba301.assignment03.dto.BookingCreateRequest;
import fu.edu.sba301.assignment03.dto.RoomRequest;
import fu.edu.sba301.assignment03.entity.RoomInformation;
import fu.edu.sba301.assignment03.entity.RoomType;
import fu.edu.sba301.assignment03.repository.RoomInformationRepository;
import fu.edu.sba301.assignment03.repository.RoomTypeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class Assignment03ApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private RoomInformationRepository roomRepository;

    private String getStaffToken() throws Exception {
        AuthLoginRequest staffReq = new AuthLoginRequest("staff@hotel.com", "staff123");
        String res = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(staffReq)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(res, AuthResponse.class).getToken();
    }

    private String getCustomerToken() throws Exception {
        AuthLoginRequest custReq = new AuthLoginRequest("customer@hotel.com", "123456");
        String res = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(custReq)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(res, AuthResponse.class).getToken();
    }

    @Test
    @DisplayName("Test 1: Public can view active room information without authentication")
    void testPublicCanViewRooms() throws Exception {
        mockMvc.perform(get("/api/rooms/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].roomStatus", is(1)));
    }

    @Test
    @DisplayName("Test 2: Staff login from application.properties returns JWT with ROLE_STAFF")
    void testStaffLoginProperties() throws Exception {
        AuthLoginRequest req = new AuthLoginRequest("staff@hotel.com", "staff123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.role", is("ROLE_STAFF")))
                .andExpect(jsonPath("$.email", is("staff@hotel.com")));
    }

    @Test
    @DisplayName("Test 3: Customer login returns JWT with ROLE_CUSTOMER")
    void testCustomerLogin() throws Exception {
        AuthLoginRequest req = new AuthLoginRequest("customer@hotel.com", "123456");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.role", is("ROLE_CUSTOMER")));
    }

    @Test
    @DisplayName("Test 4: Unauthenticated access to protected endpoint is rejected (403 Forbidden)")
    void testUnauthenticatedAccessRejected() throws Exception {
        mockMvc.perform(get("/api/admin/customers"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Test 5: Role mismatch - Customer cannot access Staff Admin endpoint (403 Forbidden)")
    void testRoleAccessDenied() throws Exception {
        String customerToken = getCustomerToken();
        mockMvc.perform(get("/api/admin/rooms")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Test 6: Staff can access Admin endpoints with valid JWT token")
    void testStaffAccessAllowed() throws Exception {
        String staffToken = getStaffToken();
        mockMvc.perform(get("/api/admin/rooms")
                        .header("Authorization", "Bearer " + staffToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    @DisplayName("Test 7: Customer creates online booking and views booking history")
    void testCustomerOnlineBookingAndHistory() throws Exception {
        String customerToken = getCustomerToken();
        RoomInformation room = roomRepository.findAll().get(0);

        BookingCreateRequest.BookingItem item = new BookingCreateRequest.BookingItem();
        item.setRoomId(room.getRoomId());
        item.setStartDate(LocalDate.now().plusDays(1));
        item.setEndDate(LocalDate.now().plusDays(3));
        item.setActualPrice(room.getRoomPricePerDay());

        BookingCreateRequest bookingReq = new BookingCreateRequest();
        bookingReq.setItems(List.of(item));

        mockMvc.perform(post("/api/customer/bookings")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingReservationId", notNullValue()))
                .andExpect(jsonPath("$.totalPrice", notNullValue()));

        // Check customer booking history
        mockMvc.perform(get("/api/customer/bookings")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    @DisplayName("Test 8: Room delete logic - permanent delete if unused, soft delete if stored in booking detail")
    void testRoomDeleteLogic() throws Exception {
        String staffToken = getStaffToken();
        String customerToken = getCustomerToken();
        RoomType rt = roomTypeRepository.findAll().get(0);

        // Case A: Create unused room and delete -> should be permanently deleted
        RoomRequest unusedReq = new RoomRequest();
        unusedReq.setRoomNumber("999");
        unusedReq.setRoomDetailDescription("Temporary testing room");
        unusedReq.setRoomMaxCapacity(2);
        unusedReq.setRoomTypeId(rt.getRoomTypeId());
        unusedReq.setRoomPricePerDay(BigDecimal.valueOf(60.00));

        String createRes = mockMvc.perform(post("/api/admin/rooms")
                        .header("Authorization", "Bearer " + staffToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unusedReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer unusedId = objectMapper.readTree(createRes).get("roomId").asInt();

        mockMvc.perform(delete("/api/admin/rooms/" + unusedId)
                        .header("Authorization", "Bearer " + staffToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hardDeleted", is(true)));

        assertFalse(roomRepository.findById(unusedId).isPresent());

        // Case B: Create another room and book it, then attempt delete -> should soft delete by setting status to 2
        RoomRequest bookedReq = new RoomRequest();
        bookedReq.setRoomNumber("888");
        bookedReq.setRoomDetailDescription("Booked room for soft delete test");
        bookedReq.setRoomMaxCapacity(2);
        bookedReq.setRoomTypeId(rt.getRoomTypeId());
        bookedReq.setRoomPricePerDay(BigDecimal.valueOf(75.00));

        String bookedRes = mockMvc.perform(post("/api/admin/rooms")
                        .header("Authorization", "Bearer " + staffToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookedReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer bookedRoomId = objectMapper.readTree(bookedRes).get("roomId").asInt();

        BookingCreateRequest.BookingItem item = new BookingCreateRequest.BookingItem();
        item.setRoomId(bookedRoomId);
        item.setStartDate(LocalDate.now().plusDays(5));
        item.setEndDate(LocalDate.now().plusDays(7));
        item.setActualPrice(BigDecimal.valueOf(75.00));

        BookingCreateRequest bookingReq = new BookingCreateRequest();
        bookingReq.setItems(List.of(item));

        mockMvc.perform(post("/api/customer/bookings")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingReq)))
                .andExpect(status().isCreated());

        // Delete room with booking history -> soft delete (status 2)
        mockMvc.perform(delete("/api/admin/rooms/" + bookedRoomId)
                        .header("Authorization", "Bearer " + staffToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hardDeleted", is(false)));

        RoomInformation softDeletedRoom = roomRepository.findById(bookedRoomId).orElseThrow();
        assertEquals((byte) 2, softDeletedRoom.getRoomStatus());
    }
}
