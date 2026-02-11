package com.bluetide.services.controller;

import com.bluetide.services.models.Inventory;
import com.bluetide.services.repository.InventoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryRepository repo;
    public InventoryController(InventoryRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Inventory> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Inventory> get(@PathVariable String id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Inventory create(@RequestBody Inventory inventory) { return repo.save(inventory); }

    @PutMapping("/{id}")
    public ResponseEntity<Inventory> update(@PathVariable String id, @RequestBody Inventory inventory) {
        return repo.findById(id).map(existing -> {
            inventory.setId(id);
            return ResponseEntity.ok(repo.save(inventory));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}