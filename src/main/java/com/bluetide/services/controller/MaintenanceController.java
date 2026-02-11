package com.bluetide.services.controller;

import com.bluetide.services.models.Maintenance;
import com.bluetide.services.repository.MaintenanceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {
    private final MaintenanceRepository repo;
    public MaintenanceController(MaintenanceRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Maintenance> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Maintenance> get(@PathVariable String id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Maintenance create(@RequestBody Maintenance m) { return repo.save(m); }

    @PutMapping("/{id}")
    public ResponseEntity<Maintenance> update(@PathVariable String id, @RequestBody Maintenance m) {
        return repo.findById(id).map(existing -> {
            m.setId(id);
            return ResponseEntity.ok(repo.save(m));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}