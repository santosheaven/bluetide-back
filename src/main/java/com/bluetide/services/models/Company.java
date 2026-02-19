package com.bluetide.services.models;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Document(collection = "companies")
public class Company {
    @Id
    private String id;

    @NotBlank(message = "{validation.company.name.required}")
    private String name;

    private List<String> managerIds;

    public Company() {
    }
}
