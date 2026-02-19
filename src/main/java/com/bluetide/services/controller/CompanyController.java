package com.bluetide.services.controller;

import com.bluetide.services.dto.ApiResponse;
import com.bluetide.services.dto.PageResponse;
import com.bluetide.services.models.Company;
import com.bluetide.services.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;
    private final MessageSource messageSource;

    public CompanyController(CompanyService companyService, MessageSource messageSource) {
        this.companyService = companyService;
        this.messageSource = messageSource;
    }

    private String msg(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<Company>>> all(Locale locale) {
        return ResponseEntity.ok(ApiResponse.success(companyService.findAll(), msg("company.retrieved.plural", locale)));
    }

    @GetMapping(params = {"page", "size"})
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<PageResponse<Company>>> allPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, Locale locale) {
        return ResponseEntity.ok(ApiResponse.success(companyService.findAllPaged(page, size), msg("company.retrieved.plural", locale)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Company>> get(@PathVariable String id, Locale locale) {
        return ResponseEntity.ok(ApiResponse.success(companyService.getById(id), msg("company.retrieved", locale)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Company>> create(@Valid @RequestBody Company company, Locale locale) {
        Company created = companyService.create(company);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, msg("company.created", locale)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Company>> update(@PathVariable String id, @Valid @RequestBody Company company, Locale locale) {
        Company updated = companyService.update(id, company);
        return ResponseEntity.ok(ApiResponse.success(updated, msg("company.updated", locale)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id, Locale locale) {
        companyService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, msg("company.deleted", locale)));
    }
}