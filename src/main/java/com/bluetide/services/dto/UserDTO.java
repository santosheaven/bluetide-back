package com.bluetide.services.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDTO {
    private String id;

    @NotBlank(message = "Display name is required")
    private String displayName;

    @Email(message = "Invalid email format")
    private String email;

    private String role;
    private String companyId;
    private String phoneNumber;
    private String profileImageUrl;
}

