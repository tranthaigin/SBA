package fu.edu.sba301.assignment03.config;

import fu.edu.sba301.assignment03.entity.Customer;
import fu.edu.sba301.assignment03.entity.RoomInformation;
import fu.edu.sba301.assignment03.entity.RoomType;
import fu.edu.sba301.assignment03.repository.CustomerRepository;
import fu.edu.sba301.assignment03.repository.RoomInformationRepository;
import fu.edu.sba301.assignment03.repository.RoomTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomInformationRepository roomRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoomTypeRepository roomTypeRepository,
                           RoomInformationRepository roomRepository,
                           CustomerRepository customerRepository,
                           PasswordEncoder passwordEncoder) {
        this.roomTypeRepository = roomTypeRepository;
        this.roomRepository = roomRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (roomTypeRepository.count() == 0) {
            RoomType standard = new RoomType(null, "Standard Room", "Single bed, city view, air conditioned", "Standard amenities");
            RoomType deluxe = new RoomType(null, "Deluxe Ocean View", "King bed, balcony ocean view, mini bar", "Complimentary breakfast");
            RoomType suite = new RoomType(null, "Presidential Suite", "2 bedrooms, living room, jacuzzi", "VIP Concierge service");
            roomTypeRepository.save(standard);
            roomTypeRepository.save(deluxe);
            roomTypeRepository.save(suite);

            if (roomRepository.count() == 0) {
                RoomInformation r101 = new RoomInformation();
                r101.setRoomNumber("101");
                r101.setRoomDetailDescription("Cozy standard room with high-speed WiFi");
                r101.setRoomMaxCapacity(2);
                r101.setRoomType(standard);
                r101.setRoomStatus((byte) 1);
                r101.setRoomPricePerDay(BigDecimal.valueOf(45.00));
                roomRepository.save(r101);

                RoomInformation r201 = new RoomInformation();
                r201.setRoomNumber("201");
                r201.setRoomDetailDescription("Spacious deluxe ocean view with private balcony");
                r201.setRoomMaxCapacity(3);
                r201.setRoomType(deluxe);
                r201.setRoomStatus((byte) 1);
                r201.setRoomPricePerDay(BigDecimal.valueOf(85.00));
                roomRepository.save(r201);

                RoomInformation r301 = new RoomInformation();
                r301.setRoomNumber("301");
                r301.setRoomDetailDescription("Luxurious presidential suite with panoramic views");
                r301.setRoomMaxCapacity(4);
                r301.setRoomType(suite);
                r301.setRoomStatus((byte) 1);
                r301.setRoomPricePerDay(BigDecimal.valueOf(250.00));
                roomRepository.save(r301);
            }
        }

        if (customerRepository.count() == 0) {
            Customer cust = new Customer();
            cust.setCustomerFullName("John Doe");
            cust.setTelephone("0909123456");
            cust.setEmailAddress("customer@hotel.com");
            cust.setCustomerBirthday(LocalDate.of(1995, 5, 20));
            cust.setCustomerStatus((byte) 1);
            cust.setPassword(passwordEncoder.encode("123456"));
            customerRepository.save(cust);
        }
    }
}
