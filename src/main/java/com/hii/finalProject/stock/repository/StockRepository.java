package com.hii.finalProject.stock.repository;

import com.hii.finalProject.products.entity.Product;
import com.hii.finalProject.stock.entity.Stock;
import com.hii.finalProject.warehouse.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockRepository extends JpaRepository <Stock, Long> {
    List<Stock> findByProduct(Product product);
//    List<Stock> findByWarehouse(Warehouse warehouse);
}
