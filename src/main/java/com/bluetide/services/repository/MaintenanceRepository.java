package com.bluetide.services.repository;

import com.bluetide.services.models.Maintenance;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface MaintenanceRepository extends MongoRepository<Maintenance, String> {
    List<Maintenance> findByInventoryId(String inventoryId);
    List<Maintenance> findByIsCompleted(Boolean isCompleted);
    List<Maintenance> findByFrequency(String frequency);
    List<Maintenance> findByIsCompletedAndDateAfter(Boolean isCompleted, Date date);
    List<Maintenance> findByDateBetween(Date startDate, Date endDate);
}
