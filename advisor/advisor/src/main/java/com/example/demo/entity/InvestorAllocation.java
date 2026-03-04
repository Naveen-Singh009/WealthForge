package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class InvestorAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long advisorId;
    private Long investorId;
   

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAdvisorId() { return advisorId; }
    public void setAdvisorId(Long advisorId) { this.advisorId = advisorId; }

    public Long getInvestorId() { return investorId; }
    public void setInvestorId(Long investorId) { this.investorId = investorId; }

    
}