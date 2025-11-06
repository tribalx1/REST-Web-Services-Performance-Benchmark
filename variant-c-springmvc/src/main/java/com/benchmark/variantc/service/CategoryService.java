package com.benchmark.variantc.service;

import com.benchmark.model.Category;
import com.benchmark.variantc.dto.CategoryDTO;
import com.benchmark.variantc.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    public Page<CategoryDTO> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(CategoryDTO::fromEntity);
    }
    
    public CategoryDTO findById(Long id) {
        return categoryRepository.findById(id)
                .map(CategoryDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));
    }
    
    @Transactional
    public CategoryDTO create(CategoryDTO dto) {
        Category category = dto.toEntity();
        Category saved = categoryRepository.save(category);
        return CategoryDTO.fromEntity(saved);
    }
    
    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));
        
        existing.setCode(dto.getCode());
        existing.setName(dto.getName());
        
        Category updated = categoryRepository.save(existing);
        return CategoryDTO.fromEntity(updated);
    }
    
    @Transactional
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
