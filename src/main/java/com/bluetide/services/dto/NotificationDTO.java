package com.bluetide.services.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
public class NotificationDTO {
    private String id;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Type is required")
    private String type;

    private String relatedEntityId;

    @NotBlank(message = "Message is required")
    private String message;

    private Boolean read;
    private Date createdAt;
}

