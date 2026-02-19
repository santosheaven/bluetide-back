package com.bluetide.services.controller;

import com.bluetide.services.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    @Value("${spring.application.name}")
    private String applicationName;

    private final MessageSource messageSource;

    public PublicController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck(Locale locale) {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("application", applicationName);
        health.put("timestamp", LocalDateTime.now());
        String msg = messageSource.getMessage("public.health", null, locale);
        return ResponseEntity.ok(ApiResponse.success(health, msg));
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> info(Locale locale) {
        Map<String, Object> info = new HashMap<>();
        info.put("name", applicationName);
        info.put("version", "1.0.0");
        info.put("description", messageSource.getMessage("public.info.description", null, locale));
        return ResponseEntity.ok(ApiResponse.success(info));
    }
}
