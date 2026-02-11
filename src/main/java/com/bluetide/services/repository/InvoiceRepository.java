package com.bluetide.services.repository;

import com.bluetide.services.models.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface InvoiceRepository extends MongoRepository<Invoice, String> {
    List<Invoice> findByPropertyId(String propertyId);
    List<Invoice> findByServiceType(String serviceType);
    List<Invoice> findByPaymentDateBetween(Date startDate, Date endDate);
    List<Invoice> findByPaymentDateIsNull();
}
