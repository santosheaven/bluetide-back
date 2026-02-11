package com.bluetide.services.repository;

import com.bluetide.services.models.ServiceRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ServiceRequestRepository extends MongoRepository<ServiceRequest, String> {
    List<ServiceRequest> findByPropertyId(String propertyId);
    List<ServiceRequest> findByRequesterId(String requesterId);
    List<ServiceRequest> findByStatus(String status);
    List<ServiceRequest> findByServiceType(String serviceType);
    List<ServiceRequest> findByPropertyIdAndStatus(String propertyId, String status);
}
