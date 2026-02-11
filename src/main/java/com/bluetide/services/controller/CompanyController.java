package com.bluetide.services.controller;

import com.bluetide.services.models.Company;
import com.bluetide.services.repository.CompanyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {
    private final CompanyRepository repo;
    public CompanyController(CompanyRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Company> all() {
        return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Company> get(@PathVariable String id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Company create(@RequestBody Company company) { return repo.save(company); }

    @PutMapping("/{id}")
    public ResponseEntity<Company> update(@PathVariable String id, @RequestBody Company company) {
        return repo.findById(id).map(existing -> {
            company.setId(id);
            return ResponseEntity.ok(repo.save(company));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}