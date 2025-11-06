package com.benchmark.varianta.dto;

import com.benchmark.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    
    private Long id;
    
    @NotBlank
    @Size(max = 32)
    private String code;
    
    @NotBlank
    @Size(max = 128)
    private String name;
    
    private LocalDateTime updatedAt;
    
    public static CategoryDTO fromEntity(Category category) {
        return new CategoryDTO(
            category.getId(),
            category.getCode(),
            category.getName(),
            category.getUpdatedAt()
        );
    }
    
    public Category toEntity() {
        Category category = new Category();
        category.setId(this.id);
        category.setCode(this.code);
        category.setName(this.name);
        category.setUpdatedAt(this.updatedAt);
        return category;
    }
}
