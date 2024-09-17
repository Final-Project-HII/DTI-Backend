package com.hii.finalProject.cartItem.repository;

import com.hii.finalProject.cartItem.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // Find CartItem by cartId and productId
    CartItem findByCartIdAndProductId(Long cartId, Long productId);

    // Delete CartItem by cartId and productId
    void deleteByCartIdAndProductId(Long cartId, Long productId);
}
