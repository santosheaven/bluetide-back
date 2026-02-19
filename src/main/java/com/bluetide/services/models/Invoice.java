package com.bluetide.services.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@Document(collection = "invoices")
public class Invoice {
    @Id
    private String id;

    @NotBlank(message = "{validation.invoice.propertyid.required}")
    private String propertyId;

    private String relatedEntityId;

    @NotBlank(message = "{validation.invoice.servicetype.required}")
    private String serviceType;

    @NotNull(message = "{validation.invoice.amount.required}")
    @Positive(message = "{validation.invoice.amount.positive}")
    private Double amount;

    private Date paymentDate;
    private String invoiceUrl;

    public Invoice() {}
}
