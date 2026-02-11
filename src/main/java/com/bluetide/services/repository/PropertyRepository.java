package com.bluetide.services.repository;

import com.bluetide.services.models.Property;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PropertyRepository extends MongoRepository<Property, String> {
    List<Property> findByOwnerId(String ownerId);
    List<Property> findByManagerId(String managerId);
    List<Property> findByType(String type);
}
