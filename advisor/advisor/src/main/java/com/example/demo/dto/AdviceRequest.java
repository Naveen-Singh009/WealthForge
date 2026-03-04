package com.example.demo.dto;

public class AdviceRequest {

    private Long investorId;
    private String question;
    private String adviceText;

    public Long getInvestorId() { return investorId; }
    public void setInvestorId(Long investorId) { this.investorId = investorId; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAdviceText() { return adviceText; }
    public void setAdviceText(String adviceText) { this.adviceText = adviceText; }
}