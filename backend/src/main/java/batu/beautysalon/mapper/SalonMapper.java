package batu.beautysalon.mapper;
import batu.beautysalon.dto.SalonDetailDto;
import batu.beautysalon.dto.SalonRequestDto;
import batu.beautysalon.dto.SalonSummaryDto;
import batu.beautysalon.model.Salon;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;


@Component
public class SalonMapper {

    private final ModelMapper modelMapper;

    public SalonMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public SalonSummaryDto toSummaryDto(Salon salon) {
        SalonSummaryDto dto = modelMapper.map(salon, SalonSummaryDto.class);
        dto.setServicesOffered(parseServices(salon.getServicesOffered()));
        return dto;
    }

    public SalonDetailDto toDetailDto(Salon salon) {
        SalonDetailDto dto = modelMapper.map(salon, SalonDetailDto.class);
        dto.setServicesOffered(parseServices(salon.getServicesOffered()));
        return dto;
    }

    public void updateEntityFromDto(SalonRequestDto requestDto, Salon entity) {
        modelMapper.map(requestDto, entity);
        // Convert the List from the frontend back into a String for the database
        entity.setServicesOffered(joinServices(requestDto.getServicesOffered()));
    }

    //utility
    public static List<String> parseServices(String raw) {
        if (raw == null || raw.isBlank()) return List.of();
        return Arrays.stream(raw.split("\\|"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public static String joinServices(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        return String.join("|", list);
    }
}