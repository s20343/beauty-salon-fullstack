package batu.beautysalon.dto.authdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenRefreshResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;

    public static TokenRefreshResponse of(String access, String refresh) {
        return TokenRefreshResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .tokenType("Bearer")
                .build();
    }
}