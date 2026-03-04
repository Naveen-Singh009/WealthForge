package com.portfolioservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePortfolioRequest {

    @NotBlank(message = "Portfolio name is required")
    @Size(max = 150, message = "Portfolio name cannot exceed 150 characters")
    private String name;

    @NotNull(message = "Initial balance is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Balance cannot be negative")
    private BigDecimal balance;
}
