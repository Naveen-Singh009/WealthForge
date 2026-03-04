package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Company;
import com.example.demo.entity.Investor;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.InvestorRepository;

@Service
public class AdminService {

    private final CompanyRepository companyRepo;
    private final InvestorRepository investorRepo;

    public AdminService(CompanyRepository companyRepo,
                        InvestorRepository investorRepo) {
        this.companyRepo = companyRepo;
        this.investorRepo = investorRepo;
    }

    // ✅ Add Company
    public Company addCompany(Company company) {

        company.setLastUpdated(LocalDateTime.now());

        return companyRepo.save(company);
    }

    // ✅ Update Company
    public Company updateCompany(Long id, Company company) {

        Company existing = companyRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Company with ID " + id + " not found"));

        existing.setCompanyName(company.getCompanyName());
        existing.setSymbol(company.getSymbol());
        existing.setSector(company.getSector());
        existing.setCurrentPrice(company.getCurrentPrice());

        // ⚠ Make sure your entity field name matches this
        existing.setAvailable_quantity(company.getAvailable_quantity());

        existing.setLastUpdated(LocalDateTime.now());

        return companyRepo.save(existing);
    }

    // ✅ Delete Company (Improved)
    public void deleteCompany(Long id) {

        Company existing = companyRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Company with ID " + id + " not found"));

        companyRepo.delete(existing);
    }

    // ✅ List Companies
    public List<Company> listCompanies() {
        return companyRepo.findAll();
    }

    // ✅ List Investors
    public List<Investor> listInvestors() {
        return investorRepo.findAll();
    }
}