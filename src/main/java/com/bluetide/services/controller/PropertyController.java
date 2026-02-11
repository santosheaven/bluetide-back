package com.bluetide.services.controller;

import com.bluetide.services.models.Property;
import com.bluetide.services.repository.PropertyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {
    private final PropertyRepository repo;
    public PropertyController(PropertyRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Property> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Property> get(@PathVariable String id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Property create(@RequestBody Property property) { return repo.save(property); }

    @PutMapping("/{id}")
    public ResponseEntity<Property> update(@PathVariable String id, @RequestBody Property property) {
        return repo.findById(id).map(existing -> {
            property.setId(id);
            return ResponseEntity.ok(repo.save(property));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}