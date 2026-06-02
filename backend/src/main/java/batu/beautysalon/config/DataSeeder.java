package batu.beautysalon.config;

import batu.beautysalon.integration.overpass.OverpassDataClient;
import batu.beautysalon.model.Role;
import batu.beautysalon.model.User;
import batu.beautysalon.repository.SalonRepository;
import batu.beautysalon.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedDatabase(
            SalonRepository salonRepository,
            OverpassDataClient overpassDataClient,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // SEED ADMIN USER
            if (!userRepository.existsByUsername("admin")) {
                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .role(Role.ROLE_ADMIN)
                        .build();

                userRepository.save(admin);
                System.out.println("Default Admin user created: admin / admin123");
            } else {
                System.out.println("Admin user already exists. Skipping user creation.");
            }

            // SEED NORMAL USER
            if (!userRepository.existsByUsername("user")) {
                User user = User.builder()
                        .username("user")
                        .password(passwordEncoder.encode("user123"))
                        .role(Role.ROLE_USER) // <-- make sure this exists in enum
                        .build();

                userRepository.save(user);
                System.out.println("Default User created: user / user123");
            } else {
                System.out.println("User already exists. Skipping user creation.");
            }

            //SEED SALONS
            if (salonRepository.count() > 0) {
                System.out.println("Database already populated with salons. Skipping data fetch.");
                return;
            }

            System.out.println("No salon data found. Fetching real Warsaw salons from OpenStreetMap...");

            var fetchedSalons = overpassDataClient.fetchSalonsInWarsaw();
            salonRepository.saveAll(fetchedSalons);

            System.out.println("Successfully saved " + fetchedSalons.size() + " salons to the database!");
        };
    }
}