package com.bluetide.services.controller;

import com.bluetide.services.models.Invoice;
import com.bluetide.services.repository.InvoiceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {
    private final InvoiceRepository repo;
    public InvoiceController(InvoiceRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Invoice> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> get(@PathVariable String id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Invoice create(@RequestBody Invoice invoice) { return repo.save(invoice); }

    @PutMapping("/{id}")
    public ResponseEntity<Invoice> update(@PathVariable String id, @RequestBody Invoice invoice) {
        return repo.findById(id).map(existing -> {
            invoice.setId(id);
            return ResponseEntity.ok(repo.save(invoice));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}