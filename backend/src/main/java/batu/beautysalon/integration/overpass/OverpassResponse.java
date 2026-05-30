package batu.beautysalon.integration.overpass;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class OverpassResponse {
    private List<OverpassElement> elements;

    @Data
    public static class OverpassElement {
        private String type;
        private Long id;
        private Double lat;
        private Double lon;
        private Map<String, String> tags;
        private OverpassCenter center;
    }

    @Data
    public static class OverpassCenter {
        private Double lat;
        private Double lon;
    }
}