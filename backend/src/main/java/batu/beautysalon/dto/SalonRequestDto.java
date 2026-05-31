package batu.beautysalon.dto;

import batu.beautysalon.model.PriceRange;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    //+48 123 456 789
    @Pattern(regexp = "^\\+?[0-9\\s\\-]{9,15}$", message = "Invalid phone number format")
    private String phoneNumber;
    private List<String> servicesOffered;
    private PriceRange priceRange;
    private String description;

}