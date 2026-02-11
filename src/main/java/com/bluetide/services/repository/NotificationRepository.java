package com.bluetide.services.repository;

import com.bluetide.services.models.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByUserId(String userId);
    List<Notification> findByUserIdAndRead(String userId, Boolean read);
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
    long countByUserIdAndRead(String userId, Boolean read);
}
