package com.benchmark.variantd.projection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;
import com.benchmark.model.Category;

import java.time.LocalDateTime;

/**
 * Projection to limit the fields returned by Spring Data REST
 * Can be used by adding ?projection=categoryExcerpt to the URL
 */
@Projection(name = "categoryExcerpt", types = { Category.class })
public interface CategoryProjection {
    
    Long getId();
    
    String getCode();
    
    String getName();
    
    LocalDateTime getUpdatedAt();
    
    @Value("#{target.items.size()}")
    int getItemCount();
}
