package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Symbol is required")
    private String symbol;

    @NotBlank(message = "Sector is required")
    private String sector;

    @NotNull(message = "Current price is required")
    @Positive(message = "Price must be positive")
    private Double currentPrice;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer available_quantity;

    private LocalDateTime lastUpdated;
}