package com.bluetide.services.controller;

import com.bluetide.services.dto.ApiResponse;
import com.bluetide.services.dto.auth.*;
import com.bluetide.services.models.User;
import com.bluetide.services.repository.UserRepository;
import com.bluetide.services.service.AuthService;
import com.bluetide.services.service.OAuth2Service;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final OAuth2Service oauth2Service;
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    public AuthController(AuthService authService, OAuth2Service oauth2Service,
                          UserRepository userRepository, MessageSource messageSource) {
        this.authService = authService;
        this.oauth2Service = oauth2Service;
        this.userRepository = userRepository;
        this.messageSource = messageSource;
    }

    private String msg(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }

    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request, Locale locale) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, msg("auth.register.success", locale)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request, Locale locale) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, msg("auth.login.success", locale)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request, Locale locale) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(response, msg("auth.refresh.success", locale)));
    }

    @PostMapping("/oauth2/google")
    public ResponseEntity<ApiResponse<AuthResponse>> googleLogin(@Valid @RequestBody OAuth2Request request, Locale locale) {
        AuthResponse response = oauth2Service.authenticateWithGoogle(request.getToken());
        return ResponseEntity.ok(ApiResponse.success(response, msg("auth.google.success", locale)));
    }

    @PostMapping("/oauth2/google/access-token")
    public ResponseEntity<ApiResponse<AuthResponse>> googleLoginWithAccessToken(@Valid @RequestBody OAuth2Request request, Locale locale) {
        AuthResponse response = oauth2Service.authenticateWithGoogleAccessToken(request.getToken());
        return ResponseEntity.ok(ApiResponse.success(response, msg("auth.google.success", locale)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(Locale locale) {
        return ResponseEntity.ok(ApiResponse.success(null, msg("auth.logout.success", locale)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse.UserInfo>> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails, Locale locale) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .role(user.getRole())
                .profileImageUrl(user.getProfileImageUrl())
                .build();

        return ResponseEntity.ok(ApiResponse.success(userInfo, msg("auth.user.info", locale)));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request, Locale locale) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow();
        authService.changePassword(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(null, msg("auth.password.changed", locale)));
    }
}
