package com.benchmark.variantc.controller;

import com.benchmark.variantc.dto.CategoryDTO;
import com.benchmark.variantc.dto.ItemDTO;
import com.benchmark.variantc.dto.PageResponse;
import com.benchmark.variantc.service.CategoryService;
import com.benchmark.variantc.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;
    private final ItemService itemService;
    
    @GetMapping
    public ResponseEntity<PageResponse<CategoryDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CategoryDTO> result = categoryService.findAll(pageable);
        return ResponseEntity.ok(PageResponse.of(result));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getById(@PathVariable Long id) {
        CategoryDTO category = categoryService.findById(id);
        return ResponseEntity.ok(category);
    }
    
    @GetMapping("/{id}/items")
    public ResponseEntity<PageResponse<ItemDTO>> getItemsByCategory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ItemDTO> result = itemService.findByCategoryId(id, pageable);
        return ResponseEntity.ok(PageResponse.of(result));
    }
    
    @PostMapping
    public ResponseEntity<CategoryDTO> create(@Valid @RequestBody CategoryDTO dto) {
        CategoryDTO created = categoryService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO dto) {
        CategoryDTO updated = categoryService.update(id, dto);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
