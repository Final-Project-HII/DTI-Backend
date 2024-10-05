package com.hii.finalProject.order.repository;

import com.hii.finalProject.order.entity.Order;
import com.hii.finalProject.order.entity.OrderStatus;
import com.hii.finalProject.salesReport.dto.SalesReportDTO;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Page<Order> findByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable);
    Page<Order> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    Page<Order> findByUserId(Long userId, Pageable pageable);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    Page<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Modifying
    @Query(value = "INSERT INTO developmentfp.orders (address_id, courier_id, created_at, final_amount, invoice_id, original_amount, status, total_quantity, total_weight, updated_at, user_id, warehouse_id) " +
            "VALUES (:#{#order.address.id}, :#{#order.courier.id}, :#{#order.createdAt}, :#{#order.finalAmount}, :#{#order.invoiceId}, :#{#order.originalAmount}, CAST(:#{#order.status.name()} AS developmentfp.order_status), :#{#order.totalQuantity}, :#{#order.totalWeight}, :#{#order.updatedAt}, :#{#order.user.id}, :#{#order.warehouse.id})", nativeQuery = true)
    void insertOrder(@Param("order") Order order);


    @Query(value = "SELECT LASTVAL()", nativeQuery = true)
    Long getLastInsertId();

    boolean existsByUserIdAndStatus(Long userId, OrderStatus status);

    //////

    List<Order> findByCreatedAtBetweenAndStatusIn(
            LocalDateTime startDate,
            LocalDateTime endDate,
            List<OrderStatus> statuses
    );

    List<Order> findByCreatedAtBetweenAndStatus(
            LocalDateTime startDate,
            LocalDateTime endDate,
            OrderStatus status
    );

    @Query("SELECT o FROM Order o WHERE (:warehouseId IS NULL OR o.warehouse.id = :warehouseId)")
    Page<Order> findByWarehouse(@Param("warehouseId") Long warehouseId, Pageable pageable);

}