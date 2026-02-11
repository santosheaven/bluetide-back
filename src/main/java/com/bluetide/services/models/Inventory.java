package com.bluetide.services.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Document(collection = "inventory")
public class Inventory {
    @Id
    private String id;
    private String propertyId;
    private String name;
    private String brand;
    private String model;
    private String state;
    private List<String> photos;

    public Inventory() {
    }
}
