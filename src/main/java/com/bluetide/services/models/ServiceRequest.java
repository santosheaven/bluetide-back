package com.bluetide.services.models;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@Document(collection = "services")
public class ServiceRequest {
    @Id
    private String id;

    @NotBlank(message = "{validation.servicerequest.propertyid.required}")
    private String propertyId;

    private String requesterId;

    @NotBlank(message = "{validation.servicerequest.servicetype.required}")
    private String serviceType;

    private String description;
    private String status;
    private Date createdAt;

    public ServiceRequest() {}
}
