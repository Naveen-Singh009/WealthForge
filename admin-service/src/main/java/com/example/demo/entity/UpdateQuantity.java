package com.example.demo.entity;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class UpdateQuantity {
//	@NotNull
//    private Long investorId;

    @NotBlank
    private String symbol;

    @NotNull
    @Min(1)
    private Integer quantity;
}
