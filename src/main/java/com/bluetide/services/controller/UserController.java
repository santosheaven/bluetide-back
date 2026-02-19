package com.bluetide.services.controller;

import com.bluetide.services.dto.ApiResponse;
import com.bluetide.services.dto.PageResponse;
import com.bluetide.services.models.User;
import com.bluetide.services.service.UserService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final MessageSource messageSource;

    public UserController(UserService userService, MessageSource messageSource) {
        this.userService = userService;
        this.messageSource = messageSource;
    }

    private String msg(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<User>>> all(Locale locale) {
        return ResponseEntity.ok(ApiResponse.success(userService.findAll(), msg("user.retrieved.plural", locale)));
    }

    @GetMapping(params = {"page", "size"})
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<PageResponse<User>>> allPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, Locale locale) {
        return ResponseEntity.ok(ApiResponse.success(userService.findAllPaged(page, size), msg("user.retrieved.plural", locale)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<User>> get(@PathVariable String id, Locale locale) {
        return ResponseEntity.ok(ApiResponse.success(userService.getById(id), msg("user.retrieved", locale)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> create(@Valid @RequestBody User user, Locale locale) {
        User created = userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, msg("user.created", locale)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> update(@PathVariable String id, @Valid @RequestBody User user, Locale locale) {
        User updated = userService.update(id, user);
        return ResponseEntity.ok(ApiResponse.success(updated, msg("user.updated", locale)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id, Locale locale) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, msg("user.deleted", locale)));
    }
}