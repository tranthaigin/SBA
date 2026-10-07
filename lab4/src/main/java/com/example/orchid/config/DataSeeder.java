package com.example.orchid.config;

import com.example.orchid.pojos.Orchid;
import com.example.orchid.pojos.OrchidCategory;
import com.example.orchid.repositories.IOrchidCategoryRepository;
import com.example.orchid.repositories.IOrchidRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner initDatabase(IOrchidCategoryRepository categoryRepository,
                                          IOrchidRepository orchidRepository) {
        return args -> {
            if (categoryRepository.count() == 0) {
                OrchidCategory cattleya = categoryRepository.save(new OrchidCategory("Cattleya"));
                OrchidCategory dendrobium = categoryRepository.save(new OrchidCategory("Dendrobium"));
                categoryRepository.save(new OrchidCategory("Phalaenopsis"));

                if (orchidRepository.count() == 0) {
                    orchidRepository.save(new Orchid(
                            null,
                            "Cattleya Queen",
                            true,
                            "Beautiful natural cattleya orchid",
                            cattleya,
                            true,
                            "https://example.com/cattleya-queen.jpg"
                    ));
                    orchidRepository.save(new Orchid(
                            null,
                            "Dendrobium Nobile",
                            false,
                            "Hybrid dendrobium orchid",
                            dendrobium,
                            true,
                            "https://example.com/dendrobium-nobile.jpg"
                    ));
                }
            }
        };
    }
}
