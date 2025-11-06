package com.benchmark.variantc.repository;

import com.benchmark.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    Optional<Item> findBySku(String sku);
    
    Page<Item> findAll(Pageable pageable);
    
    // N+1 query issue version
    Page<Item> findByCategoryId(Long categoryId, Pageable pageable);
    
    // Optimized version with JOIN FETCH
    @Query("SELECT i FROM Item i JOIN FETCH i.category WHERE i.category.id = :categoryId")
    Page<Item> findByCategoryIdWithJoinFetch(@Param("categoryId") Long categoryId, Pageable pageable);
    
    // Optimized query with JOIN FETCH for all items
    @Query(value = "SELECT i FROM Item i JOIN FETCH i.category",
           countQuery = "SELECT COUNT(i) FROM Item i")
    Page<Item> findAllWithCategory(Pageable pageable);
}
