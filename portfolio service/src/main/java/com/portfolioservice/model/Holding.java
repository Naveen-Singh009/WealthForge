package com.portfolioservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "holdings", uniqueConstraints = @UniqueConstraint(columnNames = { "portfolio_id", "asset_symbol" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "portfolio_id", nullable = false)
    private Long portfolioId;

    @Column(name = "asset_symbol", nullable = false, length = 20)
    private String assetSymbol;

    @Column(name = "asset_type", nullable = false, length = 50)
    private String assetType;

    @Column(nullable = false, precision = 20, scale = 6)
    private BigDecimal quantity;

    @Column(name = "average_price", nullable = false, precision = 20, scale = 6)
    private BigDecimal averagePrice;
}
