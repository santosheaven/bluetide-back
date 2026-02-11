package com.bluetide.services.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@Document(collection = "invoices")
public class Invoice {
    @Id
    private String id;
    private String propertyId;
    private String relatedEntityId;
    private String serviceType;
    private Double amount;
    private Date paymentDate;
    private String invoiceUrl;

    public Invoice() {}
}
