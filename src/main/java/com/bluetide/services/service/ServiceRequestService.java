package com.bluetide.services.service;

import com.bluetide.services.exception.ResourceNotFoundException;
import com.bluetide.services.models.ServiceRequest;
import com.bluetide.services.repository.ServiceRequestRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;

    public ServiceRequestService(ServiceRequestRepository serviceRequestRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
    }

    public List<ServiceRequest> findAll() {
        return serviceRequestRepository.findAll();
    }

    public Optional<ServiceRequest> findById(String id) {
        return serviceRequestRepository.findById(id);
    }

    public ServiceRequest getById(String id) {
        return serviceRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceRequest", "id", id));
    }

    public List<ServiceRequest> findByPropertyId(String propertyId) {
        return serviceRequestRepository.findByPropertyId(propertyId);
    }

    public List<ServiceRequest> findByRequesterId(String requesterId) {
        return serviceRequestRepository.findByRequesterId(requesterId);
    }

    public List<ServiceRequest> findByStatus(String status) {
        return serviceRequestRepository.findByStatus(status);
    }

    public ServiceRequest create(ServiceRequest serviceRequest) {
        serviceRequest.setCreatedAt(new Date());
        if (serviceRequest.getStatus() == null) {
            serviceRequest.setStatus("PENDING");
        }
        return serviceRequestRepository.save(serviceRequest);
    }

    public ServiceRequest update(String id, ServiceRequest serviceRequestDetails) {
        ServiceRequest existingRequest = getById(id);

        if (serviceRequestDetails.getServiceType() != null) {
            existingRequest.setServiceType(serviceRequestDetails.getServiceType());
        }
        if (serviceRequestDetails.getDescription() != null) {
            existingRequest.setDescription(serviceRequestDetails.getDescription());
        }
        if (serviceRequestDetails.getStatus() != null) {
            existingRequest.setStatus(serviceRequestDetails.getStatus());
        }

        return serviceRequestRepository.save(existingRequest);
    }

    public void delete(String id) {
        if (!serviceRequestRepository.existsById(id)) {
            throw new ResourceNotFoundException("ServiceRequest", "id", id);
        }
        serviceRequestRepository.deleteById(id);
    }

    public ServiceRequest updateStatus(String id, String status) {
        ServiceRequest serviceRequest = getById(id);
        serviceRequest.setStatus(status);
        return serviceRequestRepository.save(serviceRequest);
    }
}

