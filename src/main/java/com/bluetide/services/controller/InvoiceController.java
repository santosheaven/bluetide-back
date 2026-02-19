package com.bluetide.services.controller;

import com.bluetide.services.dto.ApiResponse;
import com.bluetide.services.dto.PageResponse;
import com.bluetide.services.models.Invoice;
import com.bluetide.services.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final MessageSource messageSource;

    public InvoiceController(InvoiceService invoiceService, MessageSource messageSource) {
        this.invoiceService = invoiceService;
        this.messageSource = messageSource;
    }

    private String msg(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OWNER', 'TENANT')")
    public ResponseEntity<ApiResponse<List<Invoice>>> all(Locale locale) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.findAll(), msg("invoice.retrieved.plural", locale)));
    }

    @GetMapping(params = {"page", "size"})
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OWNER', 'TENANT')")
    public ResponseEntity<ApiResponse<PageResponse<Invoice>>> allPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, Locale locale) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.findAllPaged(page, size), msg("invoice.retrieved.plural", locale)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OWNER', 'TENANT')")
    public ResponseEntity<ApiResponse<Invoice>> get(@PathVariable String id, Locale locale) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.getById(id), msg("invoice.retrieved", locale)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Invoice>> create(@Valid @RequestBody Invoice invoice, Locale locale) {
        Invoice created = invoiceService.create(invoice);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, msg("invoice.created", locale)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Invoice>> update(@PathVariable String id, @Valid @RequestBody Invoice invoice, Locale locale) {
        Invoice updated = invoiceService.update(id, invoice);
        return ResponseEntity.ok(ApiResponse.success(updated, msg("invoice.updated", locale)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id, Locale locale) {
        invoiceService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, msg("invoice.deleted", locale)));
    }
}