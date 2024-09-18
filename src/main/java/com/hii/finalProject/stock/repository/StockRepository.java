package com.hii.finalProject.stock.repository;

import com.hii.finalProject.products.entity.Product;
import com.hii.finalProject.stock.entity.Stock;
import com.hii.finalProject.warehouse.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository <Stock, Long> {
    List<Stock> findByProduct(Product product);
//    List<Stock> findByWarehouse(Warehouse warehouse);
//    Optional<Stock> findByWarehouseAndProduct(Warehouse warehouse, Product product);
    Optional<Stock> findByProductAndWarehouse(Product product, Warehouse warehouse);
}
