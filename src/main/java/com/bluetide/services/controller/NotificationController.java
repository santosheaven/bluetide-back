package com.bluetide.services.controller;

import com.bluetide.services.dto.ApiResponse;
import com.bluetide.services.dto.PageResponse;
import com.bluetide.services.models.Notification;
import com.bluetide.services.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final MessageSource messageSource;

    public NotificationController(NotificationService notificationService, MessageSource messageSource) {
        this.notificationService = notificationService;
        this.messageSource = messageSource;
    }

    private String msg(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OWNER', 'TENANT')")
    public ResponseEntity<ApiResponse<List<Notification>>> all(Locale locale) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.findAll(), msg("notification.retrieved.plural", locale)));
    }

    @GetMapping(params = {"page", "size"})
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OWNER', 'TENANT')")
    public ResponseEntity<ApiResponse<PageResponse<Notification>>> allPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, Locale locale) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.findAllPaged(page, size), msg("notification.retrieved.plural", locale)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OWNER', 'TENANT')")
    public ResponseEntity<ApiResponse<Notification>> get(@PathVariable String id, Locale locale) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.getById(id), msg("notification.retrieved", locale)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Notification>> create(@Valid @RequestBody Notification notification, Locale locale) {
        Notification created = notificationService.create(notification);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, msg("notification.created", locale)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OWNER', 'TENANT')")
    public ResponseEntity<ApiResponse<Notification>> update(@PathVariable String id, @Valid @RequestBody Notification notification, Locale locale) {
        Notification existing = notificationService.getById(id);
        notification.setId(id);
        if (notification.getMessage() != null) existing.setMessage(notification.getMessage());
        if (notification.getRead() != null) existing.setRead(notification.getRead());
        if (notification.getType() != null) existing.setType(notification.getType());
        Notification updated = notificationService.create(existing);
        return ResponseEntity.ok(ApiResponse.success(updated, msg("notification.updated", locale)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id, Locale locale) {
        notificationService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, msg("notification.deleted", locale)));
    }
}