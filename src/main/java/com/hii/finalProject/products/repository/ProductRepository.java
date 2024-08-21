package com.hii.finalProject.products.repository;

import com.hii.finalProject.products.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository <Product, Long>, JpaSpecificationExecutor<Product> {
//    Optional<List<Product>> findByUserId(Long userId);
}
