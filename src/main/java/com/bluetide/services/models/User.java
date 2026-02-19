package com.bluetide.services.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id; // userId (Firebase Auth)

    @Indexed(unique = true)
    @NotBlank(message = "{validation.user.email.required}")
    @Email(message = "{validation.user.email.invalid}")
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank(message = "{validation.user.displayname.required}")
    private String displayName;
    private String role;
    private String companyId;
    private List<String> ownedPropertyIds; // only for owners
    private String phoneNumber;
    private String profileImageUrl;
    private Boolean isActive;
    private String provider; // LOCAL, GOOGLE, FIREBASE
    private String providerId; // External provider user ID

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

    public User() {
        this.isActive = true;
        this.provider = "LOCAL";
    }

}