//package com.hii.finalProject.order.entity;
//
//import com.hii.finalProject.address.entity.Address;
//import com.hii.finalProject.courier.entity.Courier;
//import com.hii.finalProject.users.entity.User;
//import com.hii.finalProject.warehouse.entity.Warehouse;
//import jakarta.persistence.*;
//import lombok.Data;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//@Data
//@Entity
//@Table(name = "orders")
//public class Order {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_id_gen")
//    @SequenceGenerator(name = "order_id_gen", sequenceName = "order_id_seq", allocationSize = 1)
//    @Column(name = "id", nullable = false)
//    private Long id;
//
//    @Column(name = "invoice_id")
//    private String invoiceId;
//
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
//
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "warehouse_id", nullable = false)
//    private Warehouse warehouse;
//
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "address_id", nullable = false)
//    private Address address;
//
////    @Enumerated(EnumType.STRING)
////    @Column(name = "status", nullable = false)
////    private OrderStatus status;
//
//    @Column(name = "original_amount", nullable = false)
//    private BigDecimal originalAmount;
//
//    @Column(name = "final_amount", nullable = false)
//    private BigDecimal finalAmount;
//
//    @Column(name = "total_weight", nullable = false)
//    private Integer totalWeight;
//
//    @Column(name = "total_quantity", nullable = false)
//    private Integer totalQuantity;
//
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "courier_id", nullable = false)
//    private Courier courier;
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


package com.hii.finalProject.order.entity;

import com.hii.finalProject.orderItem.entity.OrderItem;
import com.hii.finalProject.users.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "temp_orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @Column(name = "total_amount")
    private Double totalAmount;

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