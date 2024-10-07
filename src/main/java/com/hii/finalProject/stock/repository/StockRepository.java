package com.hii.finalProject.stock.repository;

import com.hii.finalProject.products.entity.Product;
import com.hii.finalProject.stock.entity.Stock;
import com.hii.finalProject.stockMutation.entity.StockMutation;
import com.hii.finalProject.warehouse.entity.Warehouse;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository <Stock, Long>, JpaSpecificationExecutor<Stock> {
    List<Stock> findByProduct(Product product);
    Optional<Stock> findByProductAndWarehouse(Product product, Warehouse warehouse);
    //stockreport
}
