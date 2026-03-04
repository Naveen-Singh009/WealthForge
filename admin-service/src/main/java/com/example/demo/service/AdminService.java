package com.example.demo.service;

import com.example.demo.InvestorClient;
import com.example.demo.dto.InvestorDTO;
import com.example.demo.entity.Company;
import com.example.demo.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CompanyRepository companyRepo;
    private final InvestorClient investorClient;

    public Company addCompany(Company company) {
        company.setLastUpdated(LocalDateTime.now());
        return companyRepo.save(company);
    }

    public Company updateCompany(Long id, Company company) {
        Company existing = companyRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Company with ID " + id + " not found"));

        existing.setCompanyName(company.getCompanyName());
        existing.setSymbol(company.getSymbol());
        existing.setSector(company.getSector());
        existing.setCurrentPrice(company.getCurrentPrice());
        existing.setAvailable_quantity(company.getAvailable_quantity());
        existing.setLastUpdated(LocalDateTime.now());

        return companyRepo.save(existing);
    }

    public void deleteCompany(Long id) {
        Company existing = companyRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Company with ID " + id + " not found"));
        companyRepo.delete(existing);
    }

    public List<Company> listCompanies() {
        return companyRepo.findAll();
    }

    public List<InvestorDTO> listInvestors() {
        return investorClient.getAllInvestors();
    }
}
