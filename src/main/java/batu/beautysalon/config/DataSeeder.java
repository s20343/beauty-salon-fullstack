package batu.beautysalon.config;

import batu.beautysalon.model.PriceRange;
import batu.beautysalon.model.Salon;
import batu.beautysalon.repository.SalonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedDatabase(SalonRepository salonRepository) {
        return args -> {

            if (salonRepository.count() > 0) {
                return;
            }

            salonRepository.save(
                    new Salon(
                            null,
                            "Beauty Room Warsaw",
                            "Marszałkowska 10",
                            "Śródmieście",
                            "+48123456789",
                            "https://beautyroom.pl",
                            "Haircut, Nails, Makeup",
                            PriceRange.MODERATE,
                            4.8,
                            150,
                            "Modern beauty salon",
                            52.2297,
                            21.0122,
                            null,
                            null
                    )
            );

            salonRepository.save(
                    new Salon(
                            null,
                            "Perfect Nails",
                            "Puławska 55",
                            "Mokotów",
                            "+48987654321",
                            "https://perfectnails.pl",
                            "Nails, Manicure, Pedicure",
                            PriceRange.CHEAP,
                            4.5,
                            88,
                            "Specialized nail studio",
                            52.1920,
                            21.0340,
                            null,
                            null
                    )
            );
        };
    }
}