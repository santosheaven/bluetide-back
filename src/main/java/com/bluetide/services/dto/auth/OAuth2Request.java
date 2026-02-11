package com.bluetide.services.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OAuth2Request {
    @NotBlank(message = "Token is required")
    private String token;

    private String provider = "GOOGLE";
}

