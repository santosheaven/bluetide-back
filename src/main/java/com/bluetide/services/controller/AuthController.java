package com.bluetide.services.controller;

import com.bluetide.services.dto.ApiResponse;
import com.bluetide.services.dto.auth.*;
import com.bluetide.services.models.User;
import com.bluetide.services.repository.UserRepository;
import com.bluetide.services.service.AuthService;
import com.bluetide.services.service.OAuth2Service;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final OAuth2Service oauth2Service;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, OAuth2Service oauth2Service, UserRepository userRepository) {
        this.authService = authService;
        this.oauth2Service = oauth2Service;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed successfully"));
    }

    @PostMapping("/oauth2/google")
    public ResponseEntity<ApiResponse<AuthResponse>> googleLogin(@Valid @RequestBody OAuth2Request request) {
        AuthResponse response = oauth2Service.authenticateWithGoogle(request.getToken());
        return ResponseEntity.ok(ApiResponse.success(response, "Google login successful"));
    }

    @PostMapping("/oauth2/google/access-token")
    public ResponseEntity<ApiResponse<AuthResponse>> googleLoginWithAccessToken(@Valid @RequestBody OAuth2Request request) {
        AuthResponse response = oauth2Service.authenticateWithGoogleAccessToken(request.getToken());
        return ResponseEntity.ok(ApiResponse.success(response, "Google login successful"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // With JWT, logout is typically handled client-side by removing the token
        // Optionally, you could implement a token blacklist here
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse.UserInfo>> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {
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

        return ResponseEntity.ok(ApiResponse.success(userInfo, "User info retrieved"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow();
        authService.changePassword(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }
}


