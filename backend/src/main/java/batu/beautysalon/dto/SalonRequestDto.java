package batu.beautysalon.dto;

import batu.beautysalon.model.PriceRange;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalonRequestDto {

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Address cannot be blank")
    private String address;

    @NotBlank(message = "District cannot be blank")
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
}