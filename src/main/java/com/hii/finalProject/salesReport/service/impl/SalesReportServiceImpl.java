package com.hii.finalProject.salesReport.service.impl;

import com.hii.finalProject.exceptions.DataNotFoundException;

import com.hii.finalProject.order.entity.Order;
import com.hii.finalProject.order.entity.OrderStatus;
import com.hii.finalProject.order.repository.OrderRepository;

import com.hii.finalProject.orderItem.dto.OrderItemDTO;
import com.hii.finalProject.orderItem.entity.OrderItem;
import com.hii.finalProject.salesReport.dto.*;
import com.hii.finalProject.salesReport.service.SalesReportService;
import com.hii.finalProject.users.entity.User;
import com.hii.finalProject.users.repository.UserRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SalesReportServiceImpl implements SalesReportService {
    private static final Logger logger = LoggerFactory.getLogger(SalesReportServiceImpl.class);
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public SalesReportServiceImpl(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Override
    public SalesReportDTO generateDailySalesReport(LocalDate date, OrderStatus saleStatus) {
        logger.info("Generating daily sales report for date: {} and status: {}", date, saleStatus);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        return generateSalesReport(startOfDay, endOfDay, saleStatus);
    }

    @Override
    public SalesReportDTO generateMonthlySalesReport(YearMonth yearMonth, OrderStatus saleStatus) {
        logger.info("Generating monthly sales report for month: {} and status: {}", yearMonth, saleStatus);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().plusDays(1).atStartOfDay();
        return generateSalesReport(startOfMonth, endOfMonth, saleStatus);
    }

    private SalesReportDTO generateSalesReport(LocalDateTime start, LocalDateTime end, OrderStatus saleStatus) {
        List<Order> orders = orderRepository.findByCreatedAtBetweenAndStatusIn(
                start,
                end,
                List.of(saleStatus, OrderStatus.delivered)
        );

        logger.info("Found {} orders for the given criteria", orders.size());

        SalesReportDTO report = new SalesReportDTO();
        report.setStartDate(start.toLocalDate());
        report.setEndDate(end.toLocalDate().minusDays(1));
        report.setTotalOrders((long) orders.size());
        report.setTotalRevenue(calculateTotalRevenue(orders));
        report.setAverageOrderValue(calculateAverageOrderValue(orders));
        report.setTotalProductsSold(calculateTotalProductsSold(orders));
        logger.info("Generated report: {}", report);

        return report;
    }

    @Override
    public List<MonthlySales> generateYearlySalesReport(int year) {
        logger.info("Generating yearly sales report for year: {}", year);
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<Order> orders = orderRepository.findByCreatedAtBetweenAndStatus(
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX),
                OrderStatus.delivered
        );

        Map<Month, MonthlySales> monthlySalesMap = initializeMonthlyMap(year);

        for (Order order : orders) {
            Month month = order.getCreatedAt().getMonth();
            MonthlySales monthlySales = monthlySalesMap.get(month);
            monthlySales.setTotalRevenue(monthlySales.getTotalRevenue() + order.getFinalAmount().doubleValue());
            monthlySales.setTotalOrders(monthlySales.getTotalOrders() + 1);
        }

        List<MonthlySales> result = new ArrayList<>(monthlySalesMap.values());
        result.sort(Comparator.comparing(ms -> Month.valueOf(ms.getMonth())));

        logger.info("Generated yearly report with {} months of data", result.size());

        return result;
    }

    private Map<Month, MonthlySales> initializeMonthlyMap(int year) {
        Map<Month, MonthlySales> map = new EnumMap<>(Month.class);
        for (Month month : Month.values()) {
            String monthName = month.name();
            map.put(month, new MonthlySales(monthName, 0, 0));
        }
        return map;
    }

    private BigDecimal calculateTotalRevenue(List<Order> orders) {
        return orders.stream()
                .map(Order::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateAverageOrderValue(List<Order> orders) {
        if (orders.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalRevenue = calculateTotalRevenue(orders);
        return totalRevenue.divide(BigDecimal.valueOf(orders.size()), 2, RoundingMode.HALF_UP);
    }

    private Long calculateTotalProductsSold(List<Order> orders) {
        return orders.stream()
                .mapToLong(Order::getTotalQuantity)
                .sum();
    }

    private OrderDTO convertToDTO(Order order, String email) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setInvoiceId(order.getInvoiceId());
        dto.setUserId(order.getUser().getId());
        dto.setWarehouseId(order.getWarehouse().getId());
        dto.setWarehouseName(order.getWarehouse().getName());
        dto.setAddressId(order.getAddress().getId());
        dto.setItems(order.getItems().stream().map(this::convertToOrderItemDTO).collect(Collectors.toList()));
        dto.setOrderDate(order.getCreatedAt().toLocalDate());
        dto.setStatus(order.getStatus().name());
        dto.setOriginalAmount(order.getOriginalAmount());
        dto.setShippingCost(order.getShippingCost());
        dto.setFinalAmount(order.getFinalAmount());
        dto.setTotalWeight(order.getTotalWeight());
        dto.setTotalQuantity(order.getTotalQuantity());
        dto.setCourierId(order.getCourier().getId());
        dto.setCourierName(order.getCourier().getCourier());
        dto.setOriginCity(order.getWarehouse().getCity().getName());
        dto.setDestinationCity(order.getAddress().getCity().getName());
        dto.setPaymentMethod(order.getPaymentMethod());
        if (email != null) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new DataNotFoundException("User not found"));

            if (user.getWarehouse() != null && user.getWarehouse().getId() != null) {
                dto.setLoginWarehouseId(user.getWarehouse().getId());
            }
        }
        return dto;
    }

    private OrderItemDTO convertToOrderItemDTO(OrderItem orderItem) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(orderItem.getId());
        dto.setProductId(orderItem.getProduct().getId());
        dto.setProductName(orderItem.getProduct().getName());
        dto.setCategoryId(orderItem.getProduct().getCategories().getId());
        dto.setCategoryName(orderItem.getProduct().getCategories().getName());
        dto.setQuantity(orderItem.getQuantity());
        dto.setPrice(orderItem.getPrice());
        return dto;
    }

    //indah
    @Override
    public Page<OrderDTO> getAllOrders(Long warehouseId, String customerName, OrderStatus status,
                                       YearMonth month, Long productId, Long categoryId,
                                       Pageable pageable, String email) {
        Specification<Order> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (warehouseId != null) {
                predicates.add(cb.equal(root.get("warehouse").get("id"), warehouseId));
            }
            if (customerName != null && !customerName.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("user").get("name")), "%" + customerName.toLowerCase() + "%"));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (month != null) {
                LocalDateTime startDate = month.atDay(1).atStartOfDay();
                LocalDateTime endDate = month.atEndOfMonth().atTime(23, 59, 59);
                predicates.add(cb.between(root.get("createdAt"), startDate, endDate));
            }
            if (productId != null) {
                Join<Order, OrderItem> orderItemJoin = root.join("items");
                predicates.add(cb.equal(orderItemJoin.get("product").get("id"), productId));
            }
            if (categoryId != null) {
                Join<Order, OrderItem> orderItemJoin = root.join("items");
                predicates.add(cb.equal(orderItemJoin.get("product").get("categories").get("id"), categoryId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Order> orders = orderRepository.findAll(spec, pageable);
        return orders.map(order -> {
            OrderDTO dto = convertToDTO(order, email);
            dto.setMonth(YearMonth.from(order.getCreatedAt()));
            return dto;
        });
    }
    public Page<SalesSummaryDto> getSalesSummary(Long warehouseId, YearMonth month, Pageable pageable) {
        Specification<Order> spec = createBaseSpecification(warehouseId, month);
        List<Order> orders = orderRepository.findAll(spec);
        List<SalesSummaryDto> summaryList = aggregateSalesSummary(orders);
        return new PageImpl<>(summaryList, pageable, summaryList.size());
    }

    public Page<CategorySalesDto> getCategorySales(Long warehouseId, YearMonth month, Pageable pageable) {
        Specification<Order> spec = createBaseSpecification(warehouseId, month);
        List<Order> orders = orderRepository.findAll(spec);
        List<CategorySalesDto> categoryList = aggregateCategorySales(orders);
        return getPagedResult(categoryList, pageable);
    }

    public Page<ProductSalesDto> getProductSales(Long warehouseId, YearMonth month, Pageable pageable) {
        Specification<Order> spec = createBaseSpecification(warehouseId, month);
        List<Order> orders = orderRepository.findAll(spec);
        List<ProductSalesDto> productList = aggregateProductSales(orders, warehouseId);
        return getPagedResult(productList, pageable);
    }

    private <T> Page<T> getPagedResult(List<T> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }

    private Specification<Order> createBaseSpecification(Long warehouseId, YearMonth month) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (month != null) {
                LocalDateTime startDate = month.atDay(1).atStartOfDay();
                LocalDateTime endDate = month.atEndOfMonth().atTime(23, 59, 59);
                predicates.add(cb.between(root.get("createdAt"), startDate, endDate));
            }

            predicates.add(cb.equal(root.get("status"), OrderStatus.delivered));

            if (warehouseId != null) {
                predicates.add(cb.equal(root.get("warehouse").get("id"), warehouseId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private List<SalesSummaryDto> aggregateSalesSummary(List<Order> orders) {
        SalesSummaryDto summary = new SalesSummaryDto();
        summary.setMonth(orders.isEmpty() ? null : YearMonth.from(orders.get(0).getCreatedAt()));
        summary.setTotalGrossRevenue(BigDecimal.ZERO);
        summary.setTotalOrders(0);

        for (Order order : orders) {
            summary.setTotalGrossRevenue(summary.getTotalGrossRevenue().add(calculateGrossRevenue(order)));
            summary.setTotalOrders(summary.getTotalOrders() + 1);
        }

        // If we need to show "All Warehouses" in the summary
        summary.setWarehouseId(null);
        summary.setWarehouseName("All Warehouses");

        return Collections.singletonList(summary);
    }

    private List<CategorySalesDto> aggregateCategorySales(List<Order> orders) {
        Map<Long, CategorySalesDto> categoryMap = new HashMap<>();
        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                Long categoryId = item.getProduct().getCategories().getId();
                CategorySalesDto dto = categoryMap.computeIfAbsent(categoryId, k -> {
                    CategorySalesDto newDto = new CategorySalesDto();
                    newDto.setMonth(YearMonth.from(order.getCreatedAt()));
                    newDto.setCategoryId(categoryId);
                    newDto.setCategoryName(item.getProduct().getCategories().getName());
                    newDto.setTotalGrossRevenue(BigDecimal.ZERO);
                    newDto.setTotalOrders(0);
                    return newDto;
                });
                dto.setTotalGrossRevenue(dto.getTotalGrossRevenue().add(calculateItemGrossRevenue(item)));
                dto.setTotalOrders(dto.getTotalOrders() + 1);
            }
        }
        return new ArrayList<>(categoryMap.values());
    }

    private List<ProductSalesDto> aggregateProductSales(List<Order> orders, Long warehouseId) {
        Map<String, ProductSalesDto> productMap = new HashMap<>();

        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                String key = warehouseId == null ? item.getProduct().getId().toString() :
                        item.getProduct().getId() + "-" + order.getWarehouse().getId();

                ProductSalesDto dto = productMap.computeIfAbsent(key, k -> createNewProductSalesDto(item, order, warehouseId));

                dto.setTotalGrossRevenue(dto.getTotalGrossRevenue().add(calculateItemGrossRevenue(item)));
                dto.setTotalQuantity(dto.getTotalQuantity() + item.getQuantity());
            }
        }

        return new ArrayList<>(productMap.values());
    }


    private ProductSalesDto createNewProductSalesDto(OrderItem item, Order order, Long warehouseId) {
        ProductSalesDto dto = new ProductSalesDto();
        dto.setMonth(YearMonth.from(order.getCreatedAt()));
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setCategoryId(item.getProduct().getCategories().getId());
        dto.setCategoryName(item.getProduct().getCategories().getName());
        dto.setTotalGrossRevenue(BigDecimal.ZERO);
        dto.setTotalQuantity(0);
        dto.setProductPrice(BigDecimal.valueOf(item.getProduct().getPrice()));

        if (warehouseId != null) {
            dto.setWarehouseId(order.getWarehouse().getId());
            dto.setWarehouseName(order.getWarehouse().getName());
        } else {
            dto.setWarehouseId(null);
            dto.setWarehouseName("All Warehouses");
        }

        return dto;
    }

    private BigDecimal calculateGrossRevenue(Order order) {
        return order.getItems().stream()
                .map(this::calculateItemGrossRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateItemGrossRevenue(OrderItem item) {
        return BigDecimal.valueOf(item.getProduct().getPrice())
                .multiply(BigDecimal.valueOf(item.getQuantity()));
    }
}