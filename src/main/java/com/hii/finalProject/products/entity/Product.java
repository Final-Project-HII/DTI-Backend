package com.hii.finalProject.products.entity;

import com.hii.finalProject.categories.entity.Categories;
import com.hii.finalProject.image.entity.ProductImage;
import com.hii.finalProject.stock.entity.Stock;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "products", schema = "developmentfp")
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;
    private String description;
    private Integer price;
    private Integer weight;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Categories categories;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> productImages = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Stock> stocks = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Instant updatedAt = Instant.now();

//    @Column(name = "deleted_at")
//    private Instant deletedAt;

}
