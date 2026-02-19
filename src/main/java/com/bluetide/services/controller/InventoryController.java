package com.bluetide.services.controller;

import com.bluetide.services.dto.ApiResponse;
import com.bluetide.services.dto.PageResponse;
import com.bluetide.services.models.Inventory;
import com.bluetide.services.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final MessageSource messageSource;

    public InventoryController(InventoryService inventoryService, MessageSource messageSource) {
        this.inventoryService = inventoryService;
        this.messageSource = messageSource;
    }

    private String msg(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OWNER', 'TENANT')")
    public ResponseEntity<ApiResponse<List<Inventory>>> all(Locale locale) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.findAll(), msg("inventory.retrieved.plural", locale)));
    }

    @GetMapping(params = {"page", "size"})
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OWNER', 'TENANT')")
    public ResponseEntity<ApiResponse<PageResponse<Inventory>>> allPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, Locale locale) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.findAllPaged(page, size), msg("inventory.retrieved.plural", locale)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OWNER', 'TENANT')")
    public ResponseEntity<ApiResponse<Inventory>> get(@PathVariable String id, Locale locale) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getById(id), msg("inventory.retrieved", locale)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OWNER')")
    public ResponseEntity<ApiResponse<Inventory>> create(@Valid @RequestBody Inventory inventory, Locale locale) {
        Inventory created = inventoryService.create(inventory);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, msg("inventory.created", locale)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OWNER')")
    public ResponseEntity<ApiResponse<Inventory>> update(@PathVariable String id, @Valid @RequestBody Inventory inventory, Locale locale) {
        Inventory updated = inventoryService.update(id, inventory);
        return ResponseEntity.ok(ApiResponse.success(updated, msg("inventory.updated", locale)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id, Locale locale) {
        inventoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, msg("inventory.deleted", locale)));
    }
}