package com.bluetide.services.service;

import com.bluetide.services.dto.PageResponse;
import com.bluetide.services.exception.ResourceNotFoundException;
import com.bluetide.services.models.Inventory;
import com.bluetide.services.repository.InventoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

    public PageResponse<Inventory> findAllPaged(int page, int size) {
        Page<Inventory> result = inventoryRepository.findAll(PageRequest.of(page, size));
        return PageResponse.<Inventory>builder()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .first(result.isFirst())
                .last(result.isLast())
                .build();
    }

    public Optional<Inventory> findById(String id) {
        return inventoryRepository.findById(id);
    }

    public Inventory getById(String id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", id));
    }

    public List<Inventory> findByPropertyId(String propertyId) {
        return inventoryRepository.findByPropertyId(propertyId);
    }

    public List<Inventory> findByState(String state) {
        return inventoryRepository.findByState(state);
    }

    public Inventory create(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    public Inventory update(String id, Inventory inventoryDetails) {
        Inventory existingInventory = getById(id);

        if (inventoryDetails.getName() != null) {
            existingInventory.setName(inventoryDetails.getName());
        }
        if (inventoryDetails.getBrand() != null) {
            existingInventory.setBrand(inventoryDetails.getBrand());
        }
        if (inventoryDetails.getModel() != null) {
            existingInventory.setModel(inventoryDetails.getModel());
        }
        if (inventoryDetails.getState() != null) {
            existingInventory.setState(inventoryDetails.getState());
        }
        if (inventoryDetails.getPhotos() != null) {
            existingInventory.setPhotos(inventoryDetails.getPhotos());
        }

        return inventoryRepository.save(existingInventory);
    }

    public void delete(String id) {
        if (!inventoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inventory", "id", id);
        }
        inventoryRepository.deleteById(id);
    }

    public Inventory updateState(String id, String state) {
        Inventory inventory = getById(id);
        inventory.setState(state);
        return inventoryRepository.save(inventory);
    }
}

