package com.hii.finalProject.stockMutation.repository;

import com.hii.finalProject.stockMutation.entity.StockMutation;
//import com.hii.finalProject.stockMutation.entity.StockMutationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMutationRepository extends JpaRepository <StockMutation, Long>, JpaSpecificationExecutor<StockMutation> {
    List<StockMutation> findByOriginIdOrDestinationId(Long originId, Long destinationId);
//    List<StockMutation> findByStatus(StockMutationStatus status);
//    List<StockMutation> findByOriginWarehouse(Long warehouseId);
//    List<StockMutation> findByDestinationWarehouseId(Long warehouseId);

}
