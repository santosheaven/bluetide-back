package com.bluetide.services.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
public class ServiceRequestDTO {
    private String id;

    @NotBlank(message = "Property ID is required")
    private String propertyId;

    private String requesterId;

    @NotBlank(message = "Service type is required")
    private String serviceType;

    private String description;
    private String status;
    private Date createdAt;
}

