package com.benchmark.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDTO {
    
    private Long id;
    
    @NotBlank
    @Size(max = 64)
    private String sku;
    
    @NotBlank
    @Size(max = 128)
    private String name;
    
    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    @Digits(integer = 8, fraction = 2)
    private BigDecimal price;
    
    @NotNull
    @Min(0)
    private Integer stock;
    
    @NotNull
    private Long categoryId;
    
    private String categoryCode;
    private String categoryName;
    
    private LocalDateTime updatedAt;
}
