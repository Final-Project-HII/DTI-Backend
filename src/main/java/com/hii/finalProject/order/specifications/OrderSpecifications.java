package com.hii.finalProject.order.specifications;

import com.hii.finalProject.order.entity.Order;
import com.hii.finalProject.order.entity.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderSpecifications {

    public static Specification<Order> withFilters(
            OrderStatus status,
            Long warehouseId,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (warehouseId != null) {
                predicates.add(criteriaBuilder.equal(root.get("warehouse").get("id"), warehouseId));
            }

            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }

            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}