package batu.beautysalon.dto;

import batu.beautysalon.model.PriceRange;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalonRequestDto implements Serializable {

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Address cannot be blank")
    private String address;

    @NotBlank(message = "District cannot be blank")
    private String district;
    //+48123456789
    @Pattern(regexp = "^\\+48\\d{9}$", message = "Phone number must be exactly in the format +48123456789")
    private String phoneNumber;
    private List<String> servicesOffered;
    private PriceRange priceRange;
    private String description;

}