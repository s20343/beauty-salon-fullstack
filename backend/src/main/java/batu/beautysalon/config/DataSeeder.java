package batu.beautysalon.config;

import batu.beautysalon.integration.overpass.OverpassDataClient;
import batu.beautysalon.model.Role;
import batu.beautysalon.model.Salon;
import batu.beautysalon.model.User;
import batu.beautysalon.repository.SalonRepository;
import batu.beautysalon.repository.UserRepository;
import batu.beautysalon.service.SalonSnapshotService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedDatabase(
            SalonRepository salonRepository,
            OverpassDataClient overpassDataClient,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            SalonSnapshotService salonSnapshotService) {

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
                        .role(Role.ROLE_USER)
                        .build();

                userRepository.save(user);
                System.out.println("Default User created: user / user123");
            } else {
                System.out.println("User already exists. Skipping user creation.");
            }

            // SEED SALONS
            // Case 1: database already has salon data
            if (salonRepository.count() > 0) {

                // If data is normal Overpass data, do nothing.
                if (!salonSnapshotService.wasSnapshotRestored()) {
                    System.out.println("Database already has salons. Skipping fetch.");
                    return;
                }

                // If data came from snapshot fallback, try to replace it with fresh Overpass data.
                System.out.println("Database has snapshot-restored salons. Trying to refresh from Overpass...");

                try {
                    List<Salon> fetchedSalons = overpassDataClient.fetchSalonsInWarsaw();

                    if (!fetchedSalons.isEmpty()) {
                        salonRepository.deleteAll();
                        salonRepository.saveAll(fetchedSalons);

                        salonSnapshotService.saveSnapshot(fetchedSalons);
                        salonSnapshotService.clearSnapshotRestoredMarker();

                        System.out.println("Replaced snapshot data with "
                                + fetchedSalons.size()
                                + " fresh salons from Overpass.");
                        return;
                    }

                    System.out.println("Overpass returned empty list. Keeping existing snapshot-restored data.");
                } catch (Exception e) {
                    System.out.println("Overpass refresh failed. Keeping existing snapshot-restored data. Reason: "
                            + e.getMessage());
                }

                return;
            }

            // Case 2: database is empty, so try initial Overpass import
            try {
                List<Salon> fetchedSalons = overpassDataClient.fetchSalonsInWarsaw();

                if (!fetchedSalons.isEmpty()) {
                    salonRepository.saveAll(fetchedSalons);

                    salonSnapshotService.saveSnapshot(fetchedSalons);
                    salonSnapshotService.clearSnapshotRestoredMarker();

                    System.out.println("Saved " + fetchedSalons.size() + " salons from Overpass.");
                    System.out.println("Snapshot saved.");
                    return;
                }

                System.out.println("Overpass returned empty list. Loading snapshot.");

            } catch (Exception e) {
                System.out.println("Overpass failed. Loading snapshot. Reason: " + e.getMessage());
            }

            // Case 3: Overpass failed or returned empty, so restore last successful snapshot
            List<Salon> snapshotSalons = salonSnapshotService.loadSnapshot();

            if (!snapshotSalons.isEmpty()) {
                salonRepository.saveAll(snapshotSalons);
                salonSnapshotService.markSnapshotRestored();

                System.out.println("Saved " + snapshotSalons.size() + " salons from snapshot.");
                return;
            }

            System.out.println("No Overpass data and no snapshot available.");
        };
    }
}