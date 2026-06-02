package batu.beautysalon.service;

import batu.beautysalon.dto.authdto.*;
import batu.beautysalon.error.TokenRefreshException;
import batu.beautysalon.model.RefreshToken;
import batu.beautysalon.model.Role;
import batu.beautysalon.model.User;
import batu.beautysalon.repository.RefreshTokenRepository;
import batu.beautysalon.repository.UserRepository;
import batu.beautysalon.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Value("${app.jwt.refresh-token-expiration-ms}")
    private long refreshExpirationMs;


    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("Username already taken: " + req.getUsername());
        }

        User user = User.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(Role.ROLE_USER)
                .build();
        userRepository.save(user);

        return buildAuthResponse(user.getUsername());
    }


    @Transactional
    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );
        return buildAuthResponse(req.getUsername());
    }

    @Transactional
    public TokenRefreshResponse refresh(RefreshRequest req) {
        RefreshToken stored = refreshTokenRepository.findByToken(req.getRefreshToken())
                .orElseThrow(() -> new TokenRefreshException("Refresh token not found"));

        if (stored.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(stored);
            throw new TokenRefreshException("Refresh token has expired. Please log in again.");
        }

        User user = stored.getUser();
        refreshTokenRepository.delete(stored);

        String newRefreshToken = createRefreshToken(user);
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String newAccessToken = jwtUtils.generateAccessToken(userDetails);

        return TokenRefreshResponse.of(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(String username) {
        userRepository.findByUsername(username)
                .ifPresent(refreshTokenRepository::deleteByUser);
    }


    //Helpers
    private AuthResponse buildAuthResponse(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String accessToken = jwtUtils.generateAccessToken(userDetails);
        String refreshToken = createRefreshToken(userRepository.findByUsername(username).orElseThrow());
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        return AuthResponse.of(accessToken, refreshToken, username, role);
    }

    private String createRefreshToken(User user) {
        refreshTokenRepository.deleteByUser(user);

        RefreshToken rt = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiresAt(Instant.now().plusMillis(refreshExpirationMs))
                .build();
        refreshTokenRepository.save(rt);
        return rt.getToken();
    }
}
