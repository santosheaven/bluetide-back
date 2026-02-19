package com.bluetide.services.controller;

import com.bluetide.services.dto.ApiResponse;
import com.bluetide.services.dto.PageResponse;
import com.bluetide.services.models.Property;
import com.bluetide.services.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService propertyService;
    private final MessageSource messageSource;

    public PropertyController(PropertyService propertyService, MessageSource messageSource) {
        this.propertyService = propertyService;
        this.messageSource = messageSource;
    }

    private String msg(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OWNER', 'TENANT')")
    public ResponseEntity<ApiResponse<List<Property>>> all(Locale locale) {
        return ResponseEntity.ok(ApiResponse.success(propertyService.findAll(), msg("property.retrieved.plural", locale)));
    }

    @GetMapping(params = {"page", "size"})
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OWNER', 'TENANT')")
    public ResponseEntity<ApiResponse<PageResponse<Property>>> allPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, Locale locale) {
        return ResponseEntity.ok(ApiResponse.success(propertyService.findAllPaged(page, size), msg("property.retrieved.plural", locale)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OWNER', 'TENANT')")
    public ResponseEntity<ApiResponse<Property>> get(@PathVariable String id, Locale locale) {
        return ResponseEntity.ok(ApiResponse.success(propertyService.getById(id), msg("property.retrieved", locale)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Property>> create(@Valid @RequestBody Property property, Locale locale) {
        Property created = propertyService.create(property);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, msg("property.created", locale)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OWNER')")
    public ResponseEntity<ApiResponse<Property>> update(@PathVariable String id, @Valid @RequestBody Property property, Locale locale) {
        Property updated = propertyService.update(id, property);
        return ResponseEntity.ok(ApiResponse.success(updated, msg("property.updated", locale)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id, Locale locale) {
        propertyService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, msg("property.deleted", locale)));
    }
}