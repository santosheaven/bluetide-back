package com.bluetide.services.service;

import com.bluetide.services.dto.PageResponse;
import com.bluetide.services.exception.ResourceNotFoundException;
import com.bluetide.services.models.Maintenance;
import com.bluetide.services.repository.MaintenanceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;

    public MaintenanceService(MaintenanceRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
    }

    public List<Maintenance> findAll() {
        return maintenanceRepository.findAll();
    }

    public PageResponse<Maintenance> findAllPaged(int page, int size) {
        Page<Maintenance> result = maintenanceRepository.findAll(PageRequest.of(page, size));
        return PageResponse.<Maintenance>builder()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .first(result.isFirst())
                .last(result.isLast())
                .build();
    }

    public Optional<Maintenance> findById(String id) {
        return maintenanceRepository.findById(id);
    }

    public Maintenance getById(String id) {
        return maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", "id", id));
    }

    public List<Maintenance> findByInventoryId(String inventoryId) {
        return maintenanceRepository.findByInventoryId(inventoryId);
    }

    public List<Maintenance> findByIsCompleted(Boolean isCompleted) {
        return maintenanceRepository.findByIsCompleted(isCompleted);
    }

    public List<Maintenance> findUpcoming() {
        return maintenanceRepository.findByIsCompletedAndDateAfter(false, new Date());
    }

    public Maintenance create(Maintenance maintenance) {
        if (maintenance.getIsCompleted() == null) {
            maintenance.setIsCompleted(false);
        }
        return maintenanceRepository.save(maintenance);
    }

    public Maintenance update(String id, Maintenance maintenanceDetails) {
        Maintenance existingMaintenance = getById(id);

        if (maintenanceDetails.getDate() != null) {
            existingMaintenance.setDate(maintenanceDetails.getDate());
        }
        if (maintenanceDetails.getDescription() != null) {
            existingMaintenance.setDescription(maintenanceDetails.getDescription());
        }
        if (maintenanceDetails.getFrequency() != null) {
            existingMaintenance.setFrequency(maintenanceDetails.getFrequency());
        }
        if (maintenanceDetails.getIsCompleted() != null) {
            existingMaintenance.setIsCompleted(maintenanceDetails.getIsCompleted());
        }

        return maintenanceRepository.save(existingMaintenance);
    }

    public void delete(String id) {
        if (!maintenanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Maintenance", "id", id);
        }
        maintenanceRepository.deleteById(id);
    }

    public Maintenance markAsCompleted(String id) {
        Maintenance maintenance = getById(id);
        maintenance.setIsCompleted(true);
        return maintenanceRepository.save(maintenance);
    }
}

