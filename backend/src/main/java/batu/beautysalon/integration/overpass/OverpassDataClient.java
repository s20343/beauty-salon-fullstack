package batu.beautysalon.integration.overpass;

import batu.beautysalon.model.PriceRange;
import batu.beautysalon.model.Salon;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class OverpassDataClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final Random random = new Random();

    public List<Salon> fetchSalonsInWarsaw() {
        String query = """
                [out:json][timeout:25];
                area(id:3600336074)->.searchArea;
                (
                  node["shop"~"beauty|hairdresser"](area.searchArea);
                  way["shop"~"beauty|hairdresser"](area.searchArea);
                );
                out center tags;
                """;

        java.net.URI targetUrl = org.springframework.web.util.UriComponentsBuilder
                .fromUriString("https://overpass-api.de/api/interpreter")
                .queryParam("data", query)
                .build()
                .encode()
                .toUri();

        OverpassResponse response = restTemplate.getForObject(targetUrl, OverpassResponse.class);
        List<Salon> salons = new ArrayList<>();

        if (response != null && response.getElements() != null) {
            for (var element : response.getElements()) {
                Map<String, String> tags = element.getTags();

                if (tags == null || !tags.containsKey("name")) continue;

                Double lat = element.getLat() != null ? element.getLat() : element.getCenter().getLat();
                Double lon = element.getLon() != null ? element.getLon() : element.getCenter().getLon();

                String street = tags.getOrDefault("addr:street", "");
                String houseNum = tags.getOrDefault("addr:housenumber", "");
                String address = (street + " " + houseNum).trim();
                if (address.isBlank()) address = "Warsaw Center";

                String district = tags.getOrDefault("addr:suburb", "Śródmieście");
                String phone = tags.getOrDefault("phone", tags.get("contact:phone"));
                String website = tags.getOrDefault("website", tags.get("contact:website"));

                String shopType = tags.getOrDefault("shop", "");
                String beautyServices = tags.getOrDefault("beauty", "");
                String servicesOffered = shopType.equalsIgnoreCase("hairdresser") ? "Haircut, Styling" : beautyServices;
                if (servicesOffered.isBlank()) servicesOffered = "Beauty Treatments";


                //mocking review price rating
                Salon salon = Salon.builder()
                        .name(tags.get("name"))
                        .address(address)
                        .district(district)
                        .phoneNumber(phone)
                        .website(website)
                        .servicesOffered(servicesOffered)
                        .latitude(lat)
                        .longitude(lon)
                        .rating(generateRandomRating())
                        .reviewCount(random.nextInt(5, 300))
                        .priceRange(generateRandomPriceRange())
                        .description("A local beauty and hair salon located in " + district + ".")
                        .build();

                salons.add(salon);
                if (salons.size() >= 120) break;
            }
        }
        return salons;
    }

    private Double generateRandomRating() {
        return Math.round((3.5 + random.nextDouble() * 1.5) * 10.0) / 10.0;
    }

    private PriceRange generateRandomPriceRange() {
        PriceRange[] ranges = PriceRange.values();
        return ranges[random.nextInt(ranges.length)];
    }
}