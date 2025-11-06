package com.benchmark.variantd.repository;

import com.benchmark.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Optional;

@RepositoryRestResource(path = "categories", collectionResourceRel = "categories")
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    @RestResource(path = "by-code", rel = "by-code")
    Optional<Category> findByCode(String code);
    
    @Override
    Page<Category> findAll(Pageable pageable);
}
