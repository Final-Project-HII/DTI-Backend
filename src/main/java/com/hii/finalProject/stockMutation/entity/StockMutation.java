package com.hii.finalProject.stockMutation.entity;

import com.hii.finalProject.stockMutation.entity.MutationType;
import com.hii.finalProject.stockMutation.entity.StockMutationStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "stock_mutations")
public class StockMutation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stock_mutation_id_gen")
    @SequenceGenerator(name = "stock_mutation_id_gen", sequenceName = "stock_mutation_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(name = "origin", nullable = false)
    private Integer origin;

    @Column(name = "destination", nullable = false)
    private Integer destination;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "mutation_type", nullable = false)
    private MutationType mutationType = MutationType.MANUAL;

    @Column(name = "status", nullable = false)
    private StockMutationStatus status;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "requested_by")
    private Integer requestedBy;

    @Column(name = "handled_by")
    private Integer handledBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
