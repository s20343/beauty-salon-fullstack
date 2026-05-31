package batu.beautysalon.dto;

import batu.beautysalon.model.PriceRange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalonSummaryDto {
    private Long id;
    private String name;
    private String district;
    private String address;
    private Double rating;
    private Integer reviewCount;
    private PriceRange priceRange;

}
