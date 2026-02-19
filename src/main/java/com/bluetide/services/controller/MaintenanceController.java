package com.bluetide.services.controller;

import com.bluetide.services.dto.ApiResponse;
import com.bluetide.services.dto.PageResponse;
import com.bluetide.services.models.Maintenance;
import com.bluetide.services.service.MaintenanceService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;
    private final MessageSource messageSource;

    public MaintenanceController(MaintenanceService maintenanceService, MessageSource messageSource) {
        this.maintenanceService = maintenanceService;
        this.messageSource = messageSource;
    }

    private String msg(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OWNER')")
    public ResponseEntity<ApiResponse<List<Maintenance>>> all(Locale locale) {
        return ResponseEntity.ok(ApiResponse.success(maintenanceService.findAll(), msg("maintenance.retrieved.plural", locale)));
    }

    @GetMapping(params = {"page", "size"})
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OWNER')")
    public ResponseEntity<ApiResponse<PageResponse<Maintenance>>> allPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, Locale locale) {
        return ResponseEntity.ok(ApiResponse.success(maintenanceService.findAllPaged(page, size), msg("maintenance.retrieved.plural", locale)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OWNER')")
    public ResponseEntity<ApiResponse<Maintenance>> get(@PathVariable String id, Locale locale) {
        return ResponseEntity.ok(ApiResponse.success(maintenanceService.getById(id), msg("maintenance.retrieved", locale)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Maintenance>> create(@Valid @RequestBody Maintenance maintenance, Locale locale) {
        Maintenance created = maintenanceService.create(maintenance);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, msg("maintenance.created", locale)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Maintenance>> update(@PathVariable String id, @Valid @RequestBody Maintenance maintenance, Locale locale) {
        Maintenance updated = maintenanceService.update(id, maintenance);
        return ResponseEntity.ok(ApiResponse.success(updated, msg("maintenance.updated", locale)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id, Locale locale) {
        maintenanceService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, msg("maintenance.deleted", locale)));
    }
}