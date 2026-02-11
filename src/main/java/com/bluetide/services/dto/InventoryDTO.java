package com.bluetide.services.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class InventoryDTO {
    private String id;

    @NotBlank(message = "Property ID is required")
    private String propertyId;

    @NotBlank(message = "Name is required")
    private String name;

    private String brand;
    private String model;
    private String state;
    private List<String> photos;
}

