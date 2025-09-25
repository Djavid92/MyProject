package com.example.demo.DjavidMustafaev.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class IncomeDto {
    private Long id;
    private String category;
    private BigDecimal amount;
    private String description;
    private LocalDate date;
}
