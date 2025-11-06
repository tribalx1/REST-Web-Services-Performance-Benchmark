package com.benchmark.variantd.projection;

import com.benchmark.model.Item;
import org.springframework.data.rest.core.config.Projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Projection to limit the fields returned by Spring Data REST
 * Can be used by adding ?projection=itemExcerpt to the URL
 */
@Projection(name = "itemExcerpt", types = { Item.class })
public interface ItemProjection {
    
    Long getId();
    
    String getSku();
    
    String getName();
    
    BigDecimal getPrice();
    
    Integer getStock();
    
    LocalDateTime getUpdatedAt();
    
    // Nested projection for category
    CategoryInfo getCategory();
    
    interface CategoryInfo {
        Long getId();
        String getCode();
        String getName();
    }
}
