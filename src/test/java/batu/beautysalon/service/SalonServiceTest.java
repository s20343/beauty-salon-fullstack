package batu.beautysalon.service;

import batu.beautysalon.dto.SalonDetailDto;
import batu.beautysalon.dto.SalonRequestDto;
import batu.beautysalon.dto.SalonSummaryDto;
import batu.beautysalon.mapper.SalonMapper;
import batu.beautysalon.model.Salon;
import batu.beautysalon.repository.SalonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SalonServiceTest {

    @Mock
    private SalonRepository salonRepository;

    @Mock
    private SalonMapper salonMapper;

    @InjectMocks
    private SalonService salonService;

    @Test
    void shouldReturnAllSalons() {

        Salon salon = new Salon();
        salon.setId(1L);
        salon.setName("Beauty Room");

        SalonSummaryDto dto = SalonSummaryDto.builder()
                .id(1L)
                .name("Beauty Room")
                .build();

        when(salonRepository.findAll())
                .thenReturn(List.of(salon));

        when(salonMapper.toSummaryDto(salon))
                .thenReturn(dto);

        List<SalonSummaryDto> result =
                salonService.getAllSalons(null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName())
                .isEqualTo("Beauty Room");
    }
    @Test
    void shouldFilterByDistrict() {

        Salon salon = Salon.builder()
                .district("Mokotów")
                .build();

        when(salonRepository.findByDistrictIgnoreCase("Mokotów"))
                .thenReturn(List.of(salon));

        salonService.getAllSalons("Mokotów", null);

        verify(salonRepository)
                .findByDistrictIgnoreCase("Mokotów");
    }

    @Test
    void shouldFilterByService() {

        when(salonRepository
                .findByServicesOfferedContainingIgnoreCase("Nails"))
                .thenReturn(List.of());

        salonService.getAllSalons(null, "Nails");

        verify(salonRepository)
                .findByServicesOfferedContainingIgnoreCase("Nails");
    }

    @Test
    void shouldReturnSalonById() {

        Salon salon = Salon.builder()
                .id(1L)
                .name("Beauty Room")
                .build();

        SalonDetailDto dto =
                SalonDetailDto.builder()
                        .id(1L)
                        .name("Beauty Room")
                        .build();

        when(salonRepository.findById(1L))
                .thenReturn(Optional.of(salon));

        when(salonMapper.toDetailDto(salon))
                .thenReturn(dto);

        Optional<SalonDetailDto> result =
                salonService.getSalonById(1L);

        assertTrue(result.isPresent());
    }
    @Test
    void shouldReturnEmptyWhenSalonNotFound() {

        when(salonRepository.findById(99L))
                .thenReturn(Optional.empty());

        Optional<SalonDetailDto> result =
                salonService.getSalonById(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldUpdateSalon() {

        Salon salon = Salon.builder()
                .id(1L)
                .name("Old Name")
                .build();

        SalonRequestDto request =
                SalonRequestDto.builder()
                        .name("New Name")
                        .build();

        SalonDetailDto dto =
                SalonDetailDto.builder()
                        .id(1L)
                        .name("New Name")
                        .build();

        when(salonRepository.findById(1L))
                .thenReturn(Optional.of(salon));

        when(salonRepository.save(any(Salon.class)))
                .thenReturn(salon);

        when(salonMapper.toDetailDto(any()))
                .thenReturn(dto);

        Optional<SalonDetailDto> result =
                salonService.updateSalon(1L, request);

        assertTrue(result.isPresent());

        verify(salonRepository)
                .save(any(Salon.class));
    }
}