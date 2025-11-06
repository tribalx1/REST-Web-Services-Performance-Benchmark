package com.benchmark.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "item", indexes = {
    @Index(name = "idx_item_category", columnList = "category_id"),
    @Index(name = "idx_item_updated_at", columnList = "updated_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 64)
    @Column(name = "sku", unique = true, nullable = false, length = 64)
    private String sku;

    @NotBlank
    @Size(max = 128)
    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    @Digits(integer = 8, fraction = 2)
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull
    @Min(0)
    @Column(name = "stock", nullable = false)
    private Integer stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull
    private Category category;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}
