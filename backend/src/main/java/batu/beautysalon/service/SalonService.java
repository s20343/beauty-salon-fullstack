package batu.beautysalon.service;

import batu.beautysalon.dto.SalonDetailDto;
import batu.beautysalon.dto.SalonRequestDto;
import batu.beautysalon.dto.SalonSummaryDto;
import batu.beautysalon.mapper.SalonMapper;
import batu.beautysalon.model.Salon;
import batu.beautysalon.repository.SalonRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SalonService {

    private final SalonRepository salonRepository;
    private final SalonMapper salonMapper;

    public SalonService(SalonRepository salonRepository, SalonMapper salonMapper) {
        this.salonRepository = salonRepository;
        this.salonMapper = salonMapper;
    }

    public List<SalonSummaryDto> getAllSalons(String district, String service) {
        List<Salon> salons;

        if (district != null && !district.isBlank()) {
            salons = salonRepository.findByDistrictIgnoreCase(district);
        } else if (service != null && !service.isBlank()) {
            salons = salonRepository.findByServicesOfferedContainingIgnoreCase(service);
        } else {
            salons = salonRepository.findAll();
        }

        return salons.stream()
                .map(salonMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    public Optional<SalonDetailDto> getSalonById(Long id) {
        return salonRepository.findById(id)
                .map(salonMapper::toDetailDto);
    }

    public Optional<SalonDetailDto> updateSalon(Long id, SalonRequestDto requestDto) {
        return salonRepository.findById(id).map(existingSalon -> {
            salonMapper.updateEntityFromDto(requestDto, existingSalon);
            Salon updatedSalon = salonRepository.save(existingSalon);
            return salonMapper.toDetailDto(updatedSalon);
        });
    }
}