//package com.hii.finalProject.stockMutation.specification;
//
//
//import com.hii.finalProject.stockMutation.entity.StockMutation;
//import com.hii.finalProject.stockMutation.entity.StockMutationStatus;
//import org.springframework.data.jpa.domain.Specification;
//
//import java.time.LocalDateTime;
//
//public class StockMutationSpecification {
//
//    public static Specification<StockMutation> hasStatus(StockMutationStatus status) {
//        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
//    }
//
//    public static Specification<StockMutation> hasOriginWarehouse(Long warehouseId) {
//        return (root, query, cb) -> warehouseId == null ? null : cb.equal(root.get("origin").get("id"), warehouseId);
//    }
//
//    public static Specification<StockMutation> hasDestinationWarehouse(Long warehouseId) {
//        return (root, query, cb) -> warehouseId == null ? null : cb.equal(root.get("destination").get("id"), warehouseId);
//    }
//
//    public static Specification<StockMutation> hasProduct(Long productId) {
//        return (root, query, cb) -> productId == null ? null : cb.equal(root.get("product").get("id"), productId);
//    }
//
//    public static Specification<StockMutation> createdBetween(LocalDateTime start, LocalDateTime end) {
//        return (root, query, cb) -> {
//            if (start == null && end == null) return null;
//            if (start == null) return cb.lessThanOrEqualTo(root.get("createdAt"), end);
//            if (end == null) return cb.greaterThanOrEqualTo(root.get("createdAt"), start);
//            return cb.between(root.get("createdAt"), start, end);
//        };
//    }
//}