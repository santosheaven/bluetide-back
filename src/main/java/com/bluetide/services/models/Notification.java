package com.bluetide.services.models;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;

    @NotBlank(message = "{validation.notification.userid.required}")
    private String userId;

    @NotBlank(message = "{validation.notification.type.required}")
    private String type;

    private String relatedEntityId;

    @NotBlank(message = "{validation.notification.message.required}")
    private String message;

    private Boolean read;
    private Date createdAt;

    public Notification() {}
}
