package com.bluetide.services.controller;

import com.bluetide.services.dto.ApiResponse;
import com.bluetide.services.dto.PageResponse;
import com.bluetide.services.models.ServiceRequest;
import com.bluetide.services.service.ServiceRequestService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/services")
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;
    private final MessageSource messageSource;

    public ServiceRequestController(ServiceRequestService serviceRequestService, MessageSource messageSource) {
        this.serviceRequestService = serviceRequestService;
        this.messageSource = messageSource;
    }

    private String msg(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OWNER', 'TENANT')")
    public ResponseEntity<ApiResponse<List<ServiceRequest>>> all(Locale locale) {
        return ResponseEntity.ok(ApiResponse.success(serviceRequestService.findAll(), msg("servicerequest.retrieved.plural", locale)));
    }

    @GetMapping(params = {"page", "size"})
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OWNER', 'TENANT')")
    public ResponseEntity<ApiResponse<PageResponse<ServiceRequest>>> allPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, Locale locale) {
        return ResponseEntity.ok(ApiResponse.success(serviceRequestService.findAllPaged(page, size), msg("servicerequest.retrieved.plural", locale)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OWNER', 'TENANT')")
    public ResponseEntity<ApiResponse<ServiceRequest>> get(@PathVariable String id, Locale locale) {
        return ResponseEntity.ok(ApiResponse.success(serviceRequestService.getById(id), msg("servicerequest.retrieved", locale)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OWNER', 'TENANT')")
    public ResponseEntity<ApiResponse<ServiceRequest>> create(@Valid @RequestBody ServiceRequest serviceRequest, Locale locale) {
        ServiceRequest created = serviceRequestService.create(serviceRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, msg("servicerequest.created", locale)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<ServiceRequest>> update(@PathVariable String id, @Valid @RequestBody ServiceRequest serviceRequest, Locale locale) {
        ServiceRequest updated = serviceRequestService.update(id, serviceRequest);
        return ResponseEntity.ok(ApiResponse.success(updated, msg("servicerequest.updated", locale)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id, Locale locale) {
        serviceRequestService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, msg("servicerequest.deleted", locale)));
    }
}