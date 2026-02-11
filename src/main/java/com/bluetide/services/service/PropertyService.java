package com.bluetide.services.service;

import com.bluetide.services.exception.ResourceNotFoundException;
import com.bluetide.services.models.Property;
import com.bluetide.services.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public List<Property> findAll() {
        return propertyRepository.findAll();
    }

    public Optional<Property> findById(String id) {
        return propertyRepository.findById(id);
    }

    public Property getById(String id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property", "id", id));
    }

    public List<Property> findByOwnerId(String ownerId) {
        return propertyRepository.findByOwnerId(ownerId);
    }

    public List<Property> findByManagerId(String managerId) {
        return propertyRepository.findByManagerId(managerId);
    }

    public Property create(Property property) {
        return propertyRepository.save(property);
    }

    public Property update(String id, Property propertyDetails) {
        Property existingProperty = getById(id);

        if (propertyDetails.getAddress() != null) {
            existingProperty.setAddress(propertyDetails.getAddress());
        }
        if (propertyDetails.getType() != null) {
            existingProperty.setType(propertyDetails.getType());
        }
        if (propertyDetails.getEnvironments() != null) {
            existingProperty.setEnvironments(propertyDetails.getEnvironments());
        }
        if (propertyDetails.getFloors() != null) {
            existingProperty.setFloors(propertyDetails.getFloors());
        }
        if (propertyDetails.getHasPool() != null) {
            existingProperty.setHasPool(propertyDetails.getHasPool());
        }
        if (propertyDetails.getParkingSpots() != null) {
            existingProperty.setParkingSpots(propertyDetails.getParkingSpots());
        }
        if (propertyDetails.getManagerId() != null) {
            existingProperty.setManagerId(propertyDetails.getManagerId());
        }

        return propertyRepository.save(existingProperty);
    }

    public void delete(String id) {
        if (!propertyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Property", "id", id);
        }
        propertyRepository.deleteById(id);
    }

    public void updateLastMaintenance(String id) {
        Property property = getById(id);
        property.setLastMaintenance(new Date());
        propertyRepository.save(property);
    }

    public void assignManager(String propertyId, String managerId) {
        Property property = getById(propertyId);
        property.setManagerId(managerId);
        propertyRepository.save(property);
    }
}

