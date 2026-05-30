package batu.beautysalon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationErrorMessage {
    @Builder.Default
    private LocalDateTime occurrenceDateTime = LocalDateTime.now();
    private Map<String, List<String>> errors;
}