package com.example.demo.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Entity
@Table(name = "investor")
public class Investor {

	@Id
	@Column(name = "investor_id")
	private Long investorId;

	@Column(name = "investor_name")
	private String investorName;

	private BigDecimal balance;
	
	@Column(unique = true)
	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Investor() {
	}

	public Long getInvestorId() {
		return investorId;
	}

	public void setInvestorId(Long investorId) {
		this.investorId = investorId;
	}

	public String getInvestorName() {
		return investorName;
	}

	public void setInvestorName(String investorName) {
		this.investorName = investorName;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

}
