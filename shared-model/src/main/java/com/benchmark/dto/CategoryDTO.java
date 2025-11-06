package com.benchmark.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryDTO {
    
    private Long id;
    
    @NotBlank
    @Size(max = 32)
    private String code;
    
    @NotBlank
    @Size(max = 128)
    private String name;
    
    private LocalDateTime updatedAt;
}
