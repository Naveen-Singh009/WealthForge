package com.example.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.InvestorAllocation;

public interface InvestorAllocationRepository
        extends JpaRepository<InvestorAllocation, Long> {

    List<InvestorAllocation> findByAdvisorId(Long advisorId);
}