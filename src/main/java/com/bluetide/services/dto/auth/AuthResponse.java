package com.bluetide.services.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserInfo user;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInfo {
        private String id;
        private String email;
        private String displayName;
        private String role;
        private String profileImageUrl;
    }
}

