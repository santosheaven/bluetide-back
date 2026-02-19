package com.bluetide.services.service;

import com.bluetide.services.dto.PageResponse;
import com.bluetide.services.exception.ResourceNotFoundException;
import com.bluetide.services.models.Company;
import com.bluetide.services.repository.CompanyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    public PageResponse<Company> findAllPaged(int page, int size) {
        Page<Company> result = companyRepository.findAll(PageRequest.of(page, size));
        return PageResponse.<Company>builder()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .first(result.isFirst())
                .last(result.isLast())
                .build();
    }

    public Optional<Company> findById(String id) {
        return companyRepository.findById(id);
    }

    public Company getById(String id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
    }

    public Company create(Company company) {
        return companyRepository.save(company);
    }

    public Company update(String id, Company companyDetails) {
        Company existingCompany = getById(id);

        if (companyDetails.getName() != null) {
            existingCompany.setName(companyDetails.getName());
        }
        if (companyDetails.getManagerIds() != null) {
            existingCompany.setManagerIds(companyDetails.getManagerIds());
        }

        return companyRepository.save(existingCompany);
    }

    public void delete(String id) {
        if (!companyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Company", "id", id);
        }
        companyRepository.deleteById(id);
    }

    public void addManager(String companyId, String managerId) {
        Company company = getById(companyId);
        if (!company.getManagerIds().contains(managerId)) {
            company.getManagerIds().add(managerId);
            companyRepository.save(company);
        }
    }

    public void removeManager(String companyId, String managerId) {
        Company company = getById(companyId);
        company.getManagerIds().remove(managerId);
        companyRepository.save(company);
    }
}
