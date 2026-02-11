package com.bluetide.services.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@Document(collection = "properties")
public class Property {
    @Id
    private String id;
    private String address;
    private String type;
    private Integer environments;
    private Integer floors;
    private Boolean hasPool;
    private Integer parkingSpots;
    private String ownerId;
    private String managerId;
    private Date lastMaintenance;

    public Property() {
    }
}