package com.benchmark.variantc.dto;

import com.benchmark.model.Category;
import com.benchmark.model.Item;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private BigDecimal price;
    
    @NotNull
    @Min(0)
    private Integer stock;
    
    @NotNull
    private Long categoryId;
    
    private String categoryCode;
    private String categoryName;
    
    private LocalDateTime updatedAt;
    
    public static ItemDTO fromEntity(Item item) {
        return new ItemDTO(
            item.getId(),
            item.getSku(),
            item.getName(),
            item.getPrice(),
            item.getStock(),
            item.getCategory() != null ? item.getCategory().getId() : null,
            item.getCategory() != null ? item.getCategory().getCode() : null,
            item.getCategory() != null ? item.getCategory().getName() : null,
            item.getUpdatedAt()
        );
    }
    
    public Item toEntity(Category category) {
        Item item = new Item();
        item.setId(this.id);
        item.setSku(this.sku);
        item.setName(this.name);
        item.setPrice(this.price);
        item.setStock(this.stock);
        item.setCategory(category);
        item.setUpdatedAt(this.updatedAt);
        return item;
    }
}
