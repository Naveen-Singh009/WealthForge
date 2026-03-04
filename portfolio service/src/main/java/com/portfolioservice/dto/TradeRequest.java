package com.portfolioservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TradeRequest {

    @NotBlank(message = "Asset symbol is required")
    private String assetSymbol;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.000001", inclusive = true, message = "Quantity must be greater than zero")
    private BigDecimal quantity;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Price must be greater than zero")
    private BigDecimal price;

    private String assetType;
}
