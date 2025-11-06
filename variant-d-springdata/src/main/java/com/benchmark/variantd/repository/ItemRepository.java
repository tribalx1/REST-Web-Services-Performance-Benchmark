package com.benchmark.variantd.repository;

import com.benchmark.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Optional;

@RepositoryRestResource(path = "items", collectionResourceRel = "items")
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    @RestResource(path = "by-sku", rel = "by-sku")
    Optional<Item> findBySku(@Param("sku") String sku);
    
    @Override
    Page<Item> findAll(Pageable pageable);
    
    // Spring Data REST will expose this automatically as /items/search/by-category
    @RestResource(path = "by-category", rel = "by-category")
    Page<Item> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
    
    // Optimized version with JOIN FETCH
    @RestResource(exported = false) // Not exposed via REST
    @Query("SELECT i FROM Item i JOIN FETCH i.category WHERE i.category.id = :categoryId")
    Page<Item> findByCategoryIdWithJoinFetch(@Param("categoryId") Long categoryId, Pageable pageable);
    
    // Optimized query with JOIN FETCH for all items
    @RestResource(exported = false) // Not exposed via REST
    @Query(value = "SELECT i FROM Item i JOIN FETCH i.category",
           countQuery = "SELECT COUNT(i) FROM Item i")
    Page<Item> findAllWithCategory(Pageable pageable);
}
