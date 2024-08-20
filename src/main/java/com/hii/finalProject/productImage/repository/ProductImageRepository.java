package com.hii.finalProject.productImage.repository;

import com.hii.finalProject.productImage.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductId(Integer productId);
}