package com.example.demo.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.AdviceRequest;
import com.example.demo.dto.InvestorAssignRequest;
import com.example.demo.entity.Advisor;
import com.example.demo.entity.InvestorAllocation;
import com.example.demo.service.AdvisorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/advisor")
public class AdvisorController {

    private final AdvisorService service;

    
    public AdvisorController(AdvisorService service) {
        this.service = service;
    }
    
    @PostMapping("/register")
    public Advisor register(@Valid @RequestBody Advisor advisor) {
        return service.register(advisor);
    }

   

    @GetMapping("/list/{advisorId}")
    public List<Long> listAllocated(@PathVariable Long advisorId) {
        return service.getAllocatedInvestors(advisorId);
    }

    @GetMapping("/list")
    public List<Long> listAllocatedByQuery(@RequestParam Long advisorId) {
        return service.getAllocatedInvestors(advisorId);
    }
    
    @GetMapping("/list/all")
    public List<Advisor> listAllAdvicor() {
        return service.getAllAdvisor();
    }

    @PostMapping("/suggest/{advisorId}")
    public String suggest(
            @PathVariable Long advisorId,
            @RequestBody List<AdviceRequest> requests) {

        service.giveAdvice(advisorId, requests);
        return "Advice saved successfully";
    }

    @PostMapping("/advice/{advisorId}")
    public String advice(
            @PathVariable Long advisorId,
            @RequestBody List<AdviceRequest> requests) {

        service.giveAdvice(advisorId, requests);
        return "Advice saved successfully";
    }
    @PostMapping("/assign")
    public InvestorAllocation assignInvestor(
            @RequestBody InvestorAssignRequest request) {

        return service.assignInvestor(
                request.getAdvisorId(),
                request.getInvestorId());
    }

    @GetMapping("/listInvestors/{advisorId}")
    public List<Long> listAdvised(@PathVariable Long advisorId) {
        return service.getAdvisedInvestors(advisorId);
    }
}
