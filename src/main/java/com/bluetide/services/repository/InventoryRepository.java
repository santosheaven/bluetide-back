package com.bluetide.services.repository;

import com.bluetide.services.models.Inventory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface InventoryRepository extends MongoRepository<Inventory, String> {
    List<Inventory> findByPropertyId(String propertyId);
    List<Inventory> findByState(String state);
    List<Inventory> findByPropertyIdAndState(String propertyId, String state);
}
