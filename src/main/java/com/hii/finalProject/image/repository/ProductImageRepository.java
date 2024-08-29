package com.hii.finalProject.image.repository;

import com.hii.finalProject.image.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository <ProductImage, Long> {
    List<ProductImage> findByProductId (long productId);
    void deleteByProductId(Long productId);
}
