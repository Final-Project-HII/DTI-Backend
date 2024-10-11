package com.hii.finalProject.stockMutation.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hii.finalProject.order.entity.Order;
import com.hii.finalProject.products.entity.Product;
import com.hii.finalProject.stockMutationJournal.entity.StockMutationJournal;
import com.hii.finalProject.warehouse.entity.Warehouse;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "stock_mutations", schema = "developmentfp")

public class StockMutation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private Product product;

    @ManyToOne
    @JoinColumn(name = "origin", nullable = false)
    private Warehouse origin;

    @ManyToOne
    @JoinColumn(name = "destination", nullable = false)
    private Warehouse destination;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

//    @Enumerated(EnumType.STRING)
    @Column(name = "mutation_type")
    private MutationType mutationType;

//    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StockMutationStatus status;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "requested_by")
    private Integer requestedBy;

    @OneToMany(mappedBy = "stockMutation")
    @JsonBackReference
    private List<StockMutationJournal> stockMutationJournals = new ArrayList<>();

    @Column(name = "handled_by")
    private Integer handledBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

//    public enum StockMutationStatus {
//        REQUESTED,
//        APPROVED,
//        IN_TRANSIT,
//        COMPLETED,
//        CANCELLED
//    }
}