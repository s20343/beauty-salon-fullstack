package batu.beautysalon.controller;
import batu.beautysalon.dto.SalonDetailDto;
import batu.beautysalon.dto.SalonRequestDto;
import batu.beautysalon.dto.SalonSummaryDto;
import batu.beautysalon.service.SalonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/salons")
public class SalonController {

    private final SalonService salonService;

    public SalonController(SalonService salonService) {
        this.salonService = salonService;
    }

    @GetMapping
    public ResponseEntity<List<SalonSummaryDto>> getSalons(
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String service) {

        List<SalonSummaryDto> result = salonService.getAllSalons(district, service);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{salonId}")
    public ResponseEntity<SalonDetailDto> getSalonById(@PathVariable Long salonId) {
        return salonService.getSalonById(salonId)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Salon with ID " + salonId + " was not found."));
    }

    @PutMapping("/{salonId}")
    public ResponseEntity<SalonDetailDto> updateSalon(
            @PathVariable Long salonId,
            @Valid @RequestBody SalonRequestDto requestDto) {

        return salonService.updateSalon(salonId, requestDto)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
