package batu.beautysalon.integration.overpass;
import batu.beautysalon.model.PriceRange;
import batu.beautysalon.model.Salon;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class OverpassDataClient {

    private final Random random = new Random();
    private final RestTemplate restTemplate;

    public OverpassDataClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    //District bounding boxes
    private static final List<DistrictBounds> DISTRICTS = List.of(
            new DistrictBounds("Śródmieście",    52.215, 52.255, 20.990, 21.028),
            new DistrictBounds("Ochota",         52.205, 52.235, 20.958, 20.998),
            new DistrictBounds("Mokotów",        52.170, 52.220, 20.970, 21.055),
            new DistrictBounds("Żoliborz",       52.252, 52.295, 20.940, 21.000),
            new DistrictBounds("Wola",           52.218, 52.265, 20.920, 20.992),
            new DistrictBounds("Praga-Północ",   52.245, 52.285, 21.018, 21.085),
            new DistrictBounds("Praga-Południe", 52.210, 52.270, 21.025, 21.115),
            new DistrictBounds("Targówek",       52.258, 52.315, 21.042, 21.135),
            new DistrictBounds("Białołęka",      52.283, 52.365, 20.985, 21.115),
            new DistrictBounds("Bielany",        52.268, 52.335, 20.895, 20.998),
            new DistrictBounds("Bemowo",         52.210, 52.278, 20.865, 20.968),
            new DistrictBounds("Ursynów",        52.128, 52.188, 20.978, 21.080),
            new DistrictBounds("Wilanów",        52.148, 52.198, 21.055, 21.140),
            new DistrictBounds("Włochy",         52.188, 52.222, 20.918, 20.975),
            new DistrictBounds("Ursus",          52.182, 52.228, 20.860, 20.938),
            new DistrictBounds("Wawer",          52.165, 52.265, 21.090, 21.260),
            new DistrictBounds("Rembertów",      52.218, 52.272, 21.155, 21.255),
            new DistrictBounds("Wesoła",         52.228, 52.305, 21.198, 21.345)
    );

    //OSM tag to readable name service names
    private static final Map<String, String> BEAUTY_TAG_SERVICES = Map.ofEntries(
            Map.entry("nails",        "Manicure & Nails"),
            Map.entry("massage",      "Massage"),
            Map.entry("cosmetics",    "Facial Treatments"),
            Map.entry("facial",       "Facial Treatments"),
            Map.entry("eyebrows",     "Eyebrow Shaping"),
            Map.entry("eyelashes",    "Eyelash Extensions"),
            Map.entry("hair_removal", "Hair Removal"),
            Map.entry("waxing",       "Waxing"),
            Map.entry("tanning",      "Tanning"),
            Map.entry("makeup",       "Makeup"),
            Map.entry("piercing",     "Piercing"),
            Map.entry("barber",       "Barber Services")
    );

    public List<Salon> fetchSalonsInWarsaw() {
        String query = """
                [out:json][timeout:30];
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

        if (response == null || response.getElements() == null) {
            System.out.println("Warning: Overpass API returned no data.");
            return List.of();
        }
        List<Salon> salons = new ArrayList<>();

        //for duplication
        Set<String> seenKeys = new HashSet<>();

        for (OverpassResponse.OverpassElement element : response.getElements()) {
            Map<String, String> tags = element.getTags();

            if (tags == null || !tags.containsKey("name")) continue;

            Double lat = resolveLatitude(element);
            Double lon = resolveLongitude(element);
            if (lat == null || lon == null) continue;

            //Deduplication
            // Key = name + rounded lat/lon (to 3 decimal places ≈ 111m precision)
            String dedupeKey = tags.get("name").toLowerCase().trim()
                    + "|" + Math.round(lat * 1000)
                    + "|" + Math.round(lon * 1000);

            if (seenKeys.contains(dedupeKey)) continue;
            seenKeys.add(dedupeKey);

            String district = inferDistrict(tags, lat, lon);

            Salon salon = Salon.builder()
                    .name(tags.get("name"))
                    .address(buildAddress(tags))
                    .district(district)
                    .phoneNumber(resolvePhone(tags))
                    .website(resolveWebsite(tags))
                    .servicesOffered(parseServices(tags))
                    .latitude(lat)
                    .longitude(lon)
                    .rating(generateRandomRating())
                    .reviewCount(random.nextInt(5, 300))
                    .priceRange(generateRandomPriceRange())
                    .description("A local beauty and hair salon located in " + district + ", Warsaw.")
                    .build();

            salons.add(salon);

            if (salons.size() >= 120) break;
        }

        System.out.println("Fetched " + salons.size() + " unique salons from OpenStreetMap.");
        return salons;
    }




    //District
    private String inferDistrict(Map<String, String> tags, Double lat, Double lon) {

        for (String key : List.of("addr:suburb", "addr:city_district", "is_in:suburb")) {
            String value = tags.get(key);
            if (value != null && !value.isBlank() && isKnownWarsawDistrict(value)) {
                return value;
            }
        }
        return guessDistrictByCoordinates(lat, lon);
    }

    private String guessDistrictByCoordinates(Double lat, Double lon) {
        if (lat == null || lon == null) return "Warsaw";

        for (DistrictBounds d : DISTRICTS) {
            if (lat >= d.southLat && lat <= d.northLat
                    && lon >= d.westLon && lon <= d.eastLon) {
                return d.name;
            }
        }
        return "Warsaw"; // outside all known bounding boxes
    }

    private boolean isKnownWarsawDistrict(String name) {
        Set<String> known = Set.of(
                "Śródmieście", "Mokotów", "Żoliborz", "Wola",
                "Praga-Południe", "Praga-Północ", "Ursynów", "Wilanów",
                "Bemowo", "Bielany", "Ochota", "Targówek",
                "Białołęka", "Ursus", "Włochy", "Wawer",
                "Rembertów", "Wesoła"
        );
        return known.contains(name);
    }

    //Services parsing
    private String parseServices(Map<String, String> tags) {
        List<String> services = new ArrayList<>();

        String shop = tags.getOrDefault("shop", "").toLowerCase();

        if (shop.equals("hairdresser")) {
            services.add("Haircut");
            services.add("Hair Styling");

            if ("yes".equals(tags.get("colour")) || "yes".equals(tags.get("color"))) {
                services.add("Hair Coloring");
            }
        } else if (shop.equals("beauty")) {
            services.add("Beauty Treatments");
        }

        String beautyTag = tags.getOrDefault("beauty", "");
        if (!beautyTag.isBlank()) {
            for (String part : beautyTag.split("[;,]")) {
                String serviceName = BEAUTY_TAG_SERVICES.get(part.trim().toLowerCase());
                if (serviceName != null && !services.contains(serviceName)) {
                    services.add(serviceName);
                }
            }
        }

        if ("yes".equals(tags.get("male")) || "barber".equals(tags.get("beauty"))) {
            if (!services.contains("Barber Services")) {
                services.add("Barber Services");
                services.add("Beard Trimming");
            }
        }

        if (services.isEmpty()) {
            services.add("Beauty Treatments");
        }
        return String.join("| ", services);
    }

    //Address builder
    private String buildAddress(Map<String, String> tags) {
        String street    = tags.getOrDefault("addr:street", "").trim();
        String houseNum  = tags.getOrDefault("addr:housenumber", "").trim();
        String postcode  = tags.getOrDefault("addr:postcode", "").trim();
        String city      = tags.getOrDefault("addr:city", "Warsaw").trim();

        if (street.isBlank()) {
            return city;
        }
        String streetLine = houseNum.isBlank() ? street : street + " " + houseNum;

        String locationLine = postcode.isBlank()
                ? city
                : postcode + " " + city;

        return streetLine + ", " + locationLine;
    }

    //Contact field helpers
    private String resolvePhone(Map<String, String> tags) {
        String phone = tags.getOrDefault("phone", tags.get("contact:phone"));
        return (phone != null && !phone.isBlank()) ? phone : null;
    }
    private String resolveWebsite(Map<String, String> tags) {
        String website = tags.getOrDefault("website", tags.get("contact:website"));
        return (website != null && !website.isBlank()) ? website : null;
    }

    //Coordinate helpers
    private Double resolveLatitude(OverpassResponse.OverpassElement element) {
        if (element.getLat() != null) return element.getLat();
        if (element.getCenter() != null) return element.getCenter().getLat();
        return null;
    }
    private Double resolveLongitude(OverpassResponse.OverpassElement element) {
        if (element.getLon() != null) return element.getLon();
        if (element.getCenter() != null) return element.getCenter().getLon();
        return null;
    }

    //mocking rating and price
    private Double generateRandomRating() {
        return Math.round((3.5 + random.nextDouble() * 1.5) * 10.0) / 10.0;
    }

    private PriceRange generateRandomPriceRange() {
        PriceRange[] ranges = PriceRange.values();
        return ranges[random.nextInt(ranges.length)];
    }

    // Inner helper
    private record DistrictBounds(
            String name,
            double southLat,
            double northLat,
            double westLon,
            double eastLon
    ) {}
}