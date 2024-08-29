package com.hii.finalProject.products.repository;

import com.hii.finalProject.products.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository <Product, Long>, JpaSpecificationExecutor<Product> {
    @Modifying
    @Query("DELETE FROM Product p WHERE p.id = :id")
    void deleteByIdAndFlush(@Param("id") Long id);
    @Modifying
    @Query("DELETE FROM Product p WHERE p.id = :id")
    int deleteProductById(@Param("id") Long id);

//    Optional<List<Product>> findByUserId(Long userId);
}
