package com.bluetide.services.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class MaintenanceDTO {
    private String id;

    @NotBlank(message = "Inventory ID is required")
    private String inventoryId;

    @NotNull(message = "Date is required")
    private Date date;

    private String description;
    private String frequency;
    private Boolean isCompleted;
}

