package vn.edu.fpt.slot29;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "vn.edu.fpt.slot29.domain")
public class Slot29Application {
    public static void main(String[] args) {
        SpringApplication.run(Slot29Application.class, args);
    }
}
