package com.hii.finalProject.stockMutationJournal.repository;

import com.hii.finalProject.stockMutationJournal.entity.StockMutationJournal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMutationJournalRepository extends JpaRepository<StockMutationJournal, Long>, JpaSpecificationExecutor<StockMutationJournal> {
    List<StockMutationJournal> findByWarehouseIdAndStockMutation_Product_IdAndCreatedAtBetween(
            Long warehouseId, Long productId, LocalDateTime start, LocalDateTime end);

    List<StockMutationJournal> findByWarehouseIdAndCreatedAtBetween(
            Long warehouseId, LocalDateTime start, LocalDateTime end);
}