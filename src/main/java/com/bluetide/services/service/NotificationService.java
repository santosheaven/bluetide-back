package com.bluetide.services.service;

import com.bluetide.services.exception.ResourceNotFoundException;
import com.bluetide.services.models.Notification;
import com.bluetide.services.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    public Optional<Notification> findById(String id) {
        return notificationRepository.findById(id);
    }

    public Notification getById(String id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
    }

    public List<Notification> findByUserId(String userId) {
        return notificationRepository.findByUserId(userId);
    }

    public List<Notification> findByUserIdAndRead(String userId, Boolean read) {
        return notificationRepository.findByUserIdAndRead(userId, read);
    }

    public List<Notification> findUnreadByUserId(String userId) {
        return notificationRepository.findByUserIdAndRead(userId, false);
    }

    public long countUnreadByUserId(String userId) {
        return notificationRepository.countByUserIdAndRead(userId, false);
    }

    public Notification create(Notification notification) {
        notification.setCreatedAt(new Date());
        notification.setRead(false);
        return notificationRepository.save(notification);
    }

    public Notification createNotification(String userId, String type, String message, String relatedEntityId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setMessage(message);
        notification.setRelatedEntityId(relatedEntityId);
        return create(notification);
    }

    public Notification markAsRead(String id) {
        Notification notification = getById(id);
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    public void markAllAsRead(String userId) {
        List<Notification> unreadNotifications = findUnreadByUserId(userId);
        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    public void delete(String id) {
        if (!notificationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Notification", "id", id);
        }
        notificationRepository.deleteById(id);
    }

    public void deleteAllByUserId(String userId) {
        List<Notification> notifications = findByUserId(userId);
        notificationRepository.deleteAll(notifications);
    }
}

