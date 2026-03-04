package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.AdviceRequest;
import com.example.demo.entity.Advice;
import com.example.demo.entity.Advisor;
import com.example.demo.entity.InvestorAllocation;
import com.example.demo.repository.AdviceRepository;
import com.example.demo.repository.AdvisorRepository;
import com.example.demo.repository.InvestorAllocationRepository;

@Service
public class AdvisorService {

    private final AdvisorRepository advisorRepo;
    private final InvestorAllocationRepository allocationRepo;
    private final AdviceRepository adviceRepo;

    public AdvisorService(
            AdvisorRepository advisorRepo,
            InvestorAllocationRepository allocationRepo,
            AdviceRepository adviceRepo) {

        this.advisorRepo = advisorRepo;
        this.allocationRepo = allocationRepo;
        this.adviceRepo = adviceRepo;
    }

    public Advisor register(Advisor advisor) {
        return advisorRepo.save(advisor);
    }

    public List<Long> getAllocatedInvestors(Long advisorId) {
        return allocationRepo.findByAdvisorId(advisorId)
                .stream()
                .map(InvestorAllocation::getInvestorId)
                .collect(Collectors.toList());
    }

    @Transactional
    public void giveAdvice(Long advisorId, List<AdviceRequest> requests) {

        List<Advice> list = requests.stream().map(r -> {
            Advice a = new Advice();
            a.setAdvisorId(advisorId);
            a.setInvestorId(r.getInvestorId());
            a.setQuestion(r.getQuestion());
            a.setAdviceText(r.getAdviceText());
            a.setCreatedAt(LocalDateTime.now());
            return a;
        }).collect(Collectors.toList());

        adviceRepo.saveAll(list);
    }
    public InvestorAllocation assignInvestor(Long advisorId, Long investorId) {

        InvestorAllocation allocation = new InvestorAllocation();
        allocation.setAdvisorId(advisorId);
        allocation.setInvestorId(investorId);

        return allocationRepo.save(allocation);
    }

    public List<Long> getAdvisedInvestors(Long advisorId) {
        return adviceRepo.findByAdvisorId(advisorId)
                .stream()
                .map(Advice::getInvestorId)
                .distinct()
                .collect(Collectors.toList());
    }
    public List<Advisor> getAllAdvisor( ) {
        return advisorRepo.findAll().stream().toList();
    }
    
}