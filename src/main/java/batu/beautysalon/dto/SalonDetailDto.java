package batu.beautysalon.dto;
import batu.beautysalon.model.PriceRange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalonDetailDto {
    private Long id;
    private String name;
    private String address;
    private String district;
    private String phoneNumber;
    private String website;
    private List<String> servicesOffered;
    private PriceRange priceRange;
    private Double rating;
    private Integer reviewCount;
    private String description;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}