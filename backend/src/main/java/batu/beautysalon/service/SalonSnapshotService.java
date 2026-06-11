package batu.beautysalon.service;

import batu.beautysalon.model.Salon;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class SalonSnapshotService {
    private final ObjectMapper objectMapper;
    private final Path snapshotPath = Paths.get("backend", "data", "salons_snapshot.json");
    private final Path snapshotRestoredMarkerPath = Paths.get("backend", "data", "salons_snapshot_restored.flag");

    public SalonSnapshotService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void saveSnapshot(List<Salon> salons) {
        try {
            Files.createDirectories(snapshotPath.getParent());
            objectMapper.writeValue(
                    snapshotPath.toFile(),
                    salons.stream()
                            .map(this::toSnapshotSalon)
                            .toList()
            );
        } catch (IOException e) {
            System.out.println("Could not save salon snapshot: " + e.getMessage());
        }
    }

    public List<Salon> loadSnapshot() {
        try {
            if (!Files.exists(snapshotPath)) {
                return List.of();
            }

            List<Salon> salons = objectMapper.readValue(
                    snapshotPath.toFile(),
                    new TypeReference<List<Salon>>() {}
            );

            salons.forEach(salon -> {
                salon.setId(null);
                salon.setCreatedAt(null);
                salon.setUpdatedAt(null);
            });

            return salons;
        } catch (IOException e) {
            System.out.println("Could not load salon snapshot: " + e.getMessage());
            return List.of();
        }
    }

    private Salon toSnapshotSalon(Salon salon) {
        return Salon.builder()
                .name(salon.getName())
                .address(salon.getAddress())
                .district(salon.getDistrict())
                .phoneNumber(salon.getPhoneNumber())
                .website(salon.getWebsite())
                .servicesOffered(salon.getServicesOffered())
                .priceRange(salon.getPriceRange())
                .rating(salon.getRating())
                .reviewCount(salon.getReviewCount())
                .description(salon.getDescription())
                .latitude(salon.getLatitude())
                .longitude(salon.getLongitude())
                .build();
    }

    public void markSnapshotRestored() {
        try {
            Files.createDirectories(snapshotRestoredMarkerPath.getParent());
            Files.writeString(snapshotRestoredMarkerPath, "true");
        } catch (IOException e) {
            System.out.println("Could not mark snapshot restore: " + e.getMessage());
        }
    }

    public boolean wasSnapshotRestored() {
        return Files.exists(snapshotRestoredMarkerPath);
    }

    public void clearSnapshotRestoredMarker() {
        try {
            Files.deleteIfExists(snapshotRestoredMarkerPath);
        } catch (IOException e) {
            System.out.println("Could not clear snapshot restore marker: " + e.getMessage());
        }
    }
}
