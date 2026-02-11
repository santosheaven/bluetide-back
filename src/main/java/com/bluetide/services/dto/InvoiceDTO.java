package com.bluetide.services.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Date;

@Data
public class InvoiceDTO {
    private String id;

    @NotBlank(message = "Property ID is required")
    private String propertyId;

    private String relatedEntityId;

    @NotBlank(message = "Service type is required")
    private String serviceType;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    private Date paymentDate;
    private String invoiceUrl;
}

