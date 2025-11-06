package com.benchmark.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 32)
    @Column(name = "code", unique = true, nullable = false, length = 32)
    private String code;

    @NotBlank
    @Size(max = 128)
    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<Item> items = new ArrayList<>();

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}
