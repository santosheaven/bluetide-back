package com.bluetide.services.controller;

import com.bluetide.services.models.ServiceRequest;
import com.bluetide.services.repository.ServiceRequestRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceRequestController {
    private final ServiceRequestRepository repo;
    public ServiceRequestController(ServiceRequestRepository repo) { this.repo = repo; }

    @GetMapping
    public List<ServiceRequest> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceRequest> get(@PathVariable String id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ServiceRequest create(@RequestBody ServiceRequest s) { return repo.save(s); }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceRequest> update(@PathVariable String id, @RequestBody ServiceRequest s) {
        return repo.findById(id).map(existing -> {
            s.setId(id);
            return ResponseEntity.ok(repo.save(s));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}