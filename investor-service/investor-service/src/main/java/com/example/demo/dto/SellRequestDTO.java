package com.example.demo.dto;

import jakarta.validation.constraints.*;
public class SellRequestDTO {

	// Deprecated input field. Authenticated user ID from JWT is used server-side.
	private Long investorId;

	@NotNull(message = "Portfolio ID required")
	private Long portfolioId;

	@NotBlank(message="Stock name required")
	private String assetName;

	@Min(value=1,message="Quantity must be > 0")
	private int quantity;


	

	public Long getInvestorId() {
		return investorId;
	}

	public void setInvestorId(Long investorId) {
		this.investorId = investorId;
	}

	public String getAssetName() {
		return assetName;
	}

	public Long getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(Long portfolioId) {
		this.portfolioId = portfolioId;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	

}
