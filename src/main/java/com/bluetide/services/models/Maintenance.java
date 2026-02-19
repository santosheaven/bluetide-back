package com.bluetide.services.models;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@Document(collection = "maintenance")
public class Maintenance {
    @Id
    private String id;

    @NotBlank(message = "{validation.maintenance.inventoryid.required}")
    private String inventoryId;

    private Date date;

    @NotBlank(message = "{validation.maintenance.description.required}")
    private String description;

    private String frequency;
    private Boolean isCompleted;

    public Maintenance() {
    }
}