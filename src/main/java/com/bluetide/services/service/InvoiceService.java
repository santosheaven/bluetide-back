package com.bluetide.services.service;

import com.bluetide.services.exception.ResourceNotFoundException;
import com.bluetide.services.models.Invoice;
import com.bluetide.services.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public List<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

    public Optional<Invoice> findById(String id) {
        return invoiceRepository.findById(id);
    }

    public Invoice getById(String id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));
    }

    public List<Invoice> findByPropertyId(String propertyId) {
        return invoiceRepository.findByPropertyId(propertyId);
    }

    public List<Invoice> findByServiceType(String serviceType) {
        return invoiceRepository.findByServiceType(serviceType);
    }

    public Invoice create(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    public Invoice update(String id, Invoice invoiceDetails) {
        Invoice existingInvoice = getById(id);

        if (invoiceDetails.getAmount() != null) {
            existingInvoice.setAmount(invoiceDetails.getAmount());
        }
        if (invoiceDetails.getPaymentDate() != null) {
            existingInvoice.setPaymentDate(invoiceDetails.getPaymentDate());
        }
        if (invoiceDetails.getInvoiceUrl() != null) {
            existingInvoice.setInvoiceUrl(invoiceDetails.getInvoiceUrl());
        }
        if (invoiceDetails.getServiceType() != null) {
            existingInvoice.setServiceType(invoiceDetails.getServiceType());
        }

        return invoiceRepository.save(existingInvoice);
    }

    public void delete(String id) {
        if (!invoiceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Invoice", "id", id);
        }
        invoiceRepository.deleteById(id);
    }

    public Invoice markAsPaid(String id) {
        Invoice invoice = getById(id);
        invoice.setPaymentDate(new Date());
        return invoiceRepository.save(invoice);
    }
}

