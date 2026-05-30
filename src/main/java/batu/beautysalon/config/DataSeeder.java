package batu.beautysalon.config;
import batu.beautysalon.integration.overpass.OverpassDataClient;
import batu.beautysalon.repository.SalonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedDatabase(SalonRepository salonRepository, OverpassDataClient overpassDataClient) {
        return args -> {
            if (salonRepository.count() > 0) {
                System.out.println("Database already populated. Skipping data fetch.");
                return;
            }

            System.out.println("No data found. Fetching real Warsaw salons from OpenStreetMap...");

            var fetchedSalons = overpassDataClient.fetchSalonsInWarsaw();

            salonRepository.saveAll(fetchedSalons);

            System.out.println("Successfully saved " + fetchedSalons.size() + " salons to the database!");
        };
    }
}