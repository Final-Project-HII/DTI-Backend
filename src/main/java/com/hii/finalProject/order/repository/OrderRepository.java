package com.hii.finalProject.order.repository;

import com.hii.finalProject.order.entity.Order;
import com.hii.finalProject.order.entity.OrderStatus;
import com.hii.finalProject.salesReport.dto.SalesReportDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
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

    @Query("SELECT new com.hii.finalProject.salesReport.dto.SalesReportDTO(" +
            "FUNCTION('DATE', o.createdAt), COUNT(o), SUM(o.finalAmount), SUM(oi.quantity), AVG(o.finalAmount)) " +
            "FROM Order o JOIN o.items oi " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "AND o.status IN (:statuses) " +
            "GROUP BY FUNCTION('DATE', o.createdAt)")
    Page<SalesReportDTO> getDailySalesReport(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate,
                                             @Param("statuses") List<OrderStatus> statuses,
                                             Pageable pageable);

    @Query("SELECT new com.hii.finalProject.salesReport.dto.SalesReportDTO(" +
            "null, COUNT(o), SUM(o.finalAmount), SUM(oi.quantity), AVG(o.finalAmount)) " +
            "FROM Order o JOIN o.items oi " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "AND o.status IN (:statuses)")
    SalesReportDTO getOverallSalesReport(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate,
                                         @Param("statuses") List<OrderStatus> statuses);

}