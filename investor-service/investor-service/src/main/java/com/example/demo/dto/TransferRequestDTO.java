package com.example.demo.dto;

import jakarta.validation.constraints.*;

public class TransferRequestDTO {

	// Deprecated input field. Authenticated user ID from JWT is used server-side.
	private Long fromInvestorId;

	@NotNull(message="To Investor Id required")
	private Long toInvestorId;

	@Min(value=1,message="Amount must be > 0")
	private double amount;

	public Long getFromInvestorId() {
		return fromInvestorId;
	}

	public void setFromInvestorId(Long fromInvestorId) {
		this.fromInvestorId = fromInvestorId;
	}

	public Long getToInvestorId() {
		return toInvestorId;
	}

	public void setToInvestorId(Long toInvestorId) {
		this.toInvestorId = toInvestorId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

}
