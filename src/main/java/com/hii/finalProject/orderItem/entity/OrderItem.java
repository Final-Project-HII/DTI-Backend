//package com.hii.finalProject.orderItem.entity;
//
//import com.hii.finalProject.order.entity.Order;
//import com.hii.finalProject.products.entity.Product;
//import jakarta.persistence.*;
//import lombok.Data;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//@Data
//@Entity
//@Table(name = "order_items")
//public class OrderItem {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_item_id_gen")
//    @SequenceGenerator(name = "order_item_id_gen", sequenceName = "order_item_id_seq", allocationSize = 1)
//    @Column(name = "id", nullable = false)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "order_id", nullable = false)
//    private Order order;
//
//    @Column(name = "product_snapshot")
//    private String productSnapshot;
//
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "product_id", nullable = false)
//    private Product product;
//
//    @Column(name = "quantity", nullable = false)
//    private Integer quantity;
//
//    @Column(name = "price", nullable = false)
//    private BigDecimal price;
//
//    @Column(name = "created_at", nullable = false, updatable = false)
//    private LocalDateTime createdAt = LocalDateTime.now();
//
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt = LocalDateTime.now();
//
//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt = LocalDateTime.now();
//    }
//}

package com.hii.finalProject.orderItem.entity;

import com.hii.finalProject.order.entity.Order;
import com.hii.finalProject.products.entity.Product;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "temp_order_items")
@Data
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer quantity;

    private Double price;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
