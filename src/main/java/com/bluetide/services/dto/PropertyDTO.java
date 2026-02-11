package com.bluetide.services.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
public class PropertyDTO {
    private String id;

    @NotBlank(message = "Address is required")
    private String address;

    private String type;
    private Integer environments;
    private Integer floors;
    private Boolean hasPool;
    private Integer parkingSpots;
    private String ownerId;
    private String managerId;
    private Date lastMaintenance;
}

