package com.benchmark.variantc.service;

import com.benchmark.model.Category;
import com.benchmark.model.Item;
import com.benchmark.variantc.dto.ItemDTO;
import com.benchmark.variantc.repository.CategoryRepository;
import com.benchmark.variantc.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
    
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    
    @Value("${app.use-join-fetch:true}")
    private boolean useJoinFetch;
    
    public Page<ItemDTO> findAll(Pageable pageable) {
        Page<Item> items = useJoinFetch 
            ? itemRepository.findAllWithCategory(pageable)
            : itemRepository.findAll(pageable);
        
        return items.map(ItemDTO::fromEntity);
    }
    
    public ItemDTO findById(Long id) {
        return itemRepository.findById(id)
                .map(ItemDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("Item not found: " + id));
    }
    
    public Page<ItemDTO> findByCategoryId(Long categoryId, Pageable pageable) {
        Page<Item> items = useJoinFetch 
            ? itemRepository.findByCategoryIdWithJoinFetch(categoryId, pageable)
            : itemRepository.findByCategoryId(categoryId, pageable);
        
        return items.map(ItemDTO::fromEntity);
    }
    
    @Transactional
    public ItemDTO create(ItemDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found: " + dto.getCategoryId()));
        
        Item item = dto.toEntity(category);
        Item saved = itemRepository.save(item);
        return ItemDTO.fromEntity(saved);
    }
    
    @Transactional
    public ItemDTO update(Long id, ItemDTO dto) {
        Item existing = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found: " + id));
        
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found: " + dto.getCategoryId()));
        
        existing.setSku(dto.getSku());
        existing.setName(dto.getName());
        existing.setPrice(dto.getPrice());
        existing.setStock(dto.getStock());
        existing.setCategory(category);
        
        Item updated = itemRepository.save(existing);
        return ItemDTO.fromEntity(updated);
    }
    
    @Transactional
    public void delete(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new RuntimeException("Item not found: " + id);
        }
        itemRepository.deleteById(id);
    }
}
