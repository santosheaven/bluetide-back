package com.bluetide.services.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@Document(collection = "services")
public class ServiceRequest {
    @Id
    private String id;
    private String propertyId;
    private String requesterId;
    private String serviceType;
    private String description;
    private String status;
    private Date createdAt;

    public ServiceRequest() {}
}
