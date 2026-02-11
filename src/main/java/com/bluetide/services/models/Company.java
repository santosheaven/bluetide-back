package com.bluetide.services.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Document(collection = "companies")
public class Company {
    @Id
    private String id;
    private String name;
    private List<String> managerIds;

    public Company() {
    }
}
