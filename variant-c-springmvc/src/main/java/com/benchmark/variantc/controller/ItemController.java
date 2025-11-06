package com.benchmark.variantc.controller;

import com.benchmark.variantc.dto.ItemDTO;
import com.benchmark.variantc.dto.PageResponse;
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
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    
    private final ItemService itemService;
    
    @GetMapping
    public ResponseEntity<PageResponse<ItemDTO>> getAll(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ItemDTO> result;
        
        if (categoryId != null) {
            result = itemService.findByCategoryId(categoryId, pageable);
        } else {
            result = itemService.findAll(pageable);
        }
        
        return ResponseEntity.ok(PageResponse.of(result));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> getById(@PathVariable Long id) {
        ItemDTO item = itemService.findById(id);
        return ResponseEntity.ok(item);
    }
    
    @PostMapping
    public ResponseEntity<ItemDTO> create(@Valid @RequestBody ItemDTO dto) {
        ItemDTO created = itemService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ItemDTO dto) {
        ItemDTO updated = itemService.update(id, dto);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
