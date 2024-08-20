package com.hii.finalProject.stockMutationJournal.entity;

import com.hii.finalProject.stockMutation.entity.StockMutation;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "stock_mutation_journals")
public class StockMutationJournal {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stock_mutation_journal_id_gen")
    @SequenceGenerator(name = "stock_mutation_journal_id_gen", sequenceName = "stock_mutation_journal_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "stock_mutation_id", nullable = false)
    private StockMutation stockMutation;

    @Column(name = "warehouse_id", nullable = false)
    private Integer warehouseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "mutation_type", nullable = false)
    private MutationType mutationType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum MutationType {
        IN,
        OUT;
    }
}
