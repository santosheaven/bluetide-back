package com.bluetide.services.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CompanyDTO {
    private String id;

    @NotBlank(message = "Company name is required")
    private String name;

    private List<String> managerIds;
}

