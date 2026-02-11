package com.bluetide.services.service;

import com.bluetide.services.dto.auth.AuthResponse;
import com.bluetide.services.exception.BadRequestException;
import com.bluetide.services.models.User;
import com.bluetide.services.repository.UserRepository;
import com.bluetide.services.security.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

@Service
public class OAuth2Service {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final RestTemplate restTemplate;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private static final String GOOGLE_TOKEN_INFO_URL = "https://oauth2.googleapis.com/tokeninfo?id_token=";
    private static final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";

    public OAuth2Service(UserRepository userRepository, JwtUtils jwtUtils, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.restTemplate = restTemplate;
    }

    public AuthResponse authenticateWithGoogle(String idToken) {
        // Validate the Google ID token and get user info
        GoogleUserInfo googleUserInfo = validateGoogleIdToken(idToken);

        if (googleUserInfo == null) {
            throw new BadRequestException("Invalid Google token");
        }

        // Find or create user
        User user = userRepository.findByProviderAndProviderId("GOOGLE", googleUserInfo.sub)
                .orElseGet(() -> userRepository.findByEmail(googleUserInfo.email)
                        .orElseGet(() -> createUserFromGoogle(googleUserInfo)));

        // Generate JWT tokens
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                "",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()))
        );

        String accessToken = jwtUtils.generateToken(userDetails, user.getId(), user.getRole());
        String refreshToken = jwtUtils.generateRefreshToken(userDetails, user.getId());

        return buildAuthResponse(user, accessToken, refreshToken);
    }

    public AuthResponse authenticateWithGoogleAccessToken(String accessToken) {
        // Use access token to get user info from Google
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    GOOGLE_USER_INFO_URL,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            Map<String, Object> userInfo = response.getBody();
            if (userInfo == null) {
                throw new BadRequestException("Failed to get user info from Google");
            }

            GoogleUserInfo googleUserInfo = new GoogleUserInfo();
            googleUserInfo.sub = (String) userInfo.get("sub");
            googleUserInfo.email = (String) userInfo.get("email");
            googleUserInfo.name = (String) userInfo.get("name");
            googleUserInfo.picture = (String) userInfo.get("picture");
            googleUserInfo.emailVerified = Boolean.parseBoolean(String.valueOf(userInfo.get("email_verified")));

            // Find or create user
            User user = userRepository.findByProviderAndProviderId("GOOGLE", googleUserInfo.sub)
                    .orElseGet(() -> userRepository.findByEmail(googleUserInfo.email)
                            .orElseGet(() -> createUserFromGoogle(googleUserInfo)));

            // Generate JWT tokens
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    "",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()))
            );

            String jwtAccessToken = jwtUtils.generateToken(userDetails, user.getId(), user.getRole());
            String jwtRefreshToken = jwtUtils.generateRefreshToken(userDetails, user.getId());

            return buildAuthResponse(user, jwtAccessToken, jwtRefreshToken);
        } catch (Exception e) {
            throw new BadRequestException("Failed to authenticate with Google: " + e.getMessage());
        }
    }

    private GoogleUserInfo validateGoogleIdToken(String idToken) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    GOOGLE_TOKEN_INFO_URL + idToken,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> tokenInfo = response.getBody();
                GoogleUserInfo userInfo = new GoogleUserInfo();
                userInfo.sub = (String) tokenInfo.get("sub");
                userInfo.email = (String) tokenInfo.get("email");
                userInfo.name = (String) tokenInfo.get("name");
                userInfo.picture = (String) tokenInfo.get("picture");
                userInfo.emailVerified = "true".equals(tokenInfo.get("email_verified"));
                return userInfo;
            }
        } catch (Exception e) {
            throw new BadRequestException("Failed to validate Google token: " + e.getMessage());
        }
        return null;
    }

    private User createUserFromGoogle(GoogleUserInfo googleUserInfo) {
        User user = new User();
        user.setEmail(googleUserInfo.email);
        user.setDisplayName(googleUserInfo.name);
        user.setProfileImageUrl(googleUserInfo.picture);
        user.setRole("OWNER");
        user.setProvider("GOOGLE");
        user.setProviderId(googleUserInfo.sub);
        user.setIsActive(true);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        return userRepository.save(user);
    }

    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpiration)
                .user(AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .displayName(user.getDisplayName())
                        .role(user.getRole())
                        .profileImageUrl(user.getProfileImageUrl())
                        .build())
                .build();
    }

    private static class GoogleUserInfo {
        String sub;
        String email;
        String name;
        String picture;
        boolean emailVerified;
    }
}

