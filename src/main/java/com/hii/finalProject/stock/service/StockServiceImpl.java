package com.hii.finalProject.stock.service;

import com.hii.finalProject.exceptions.DataNotFoundException;
import com.hii.finalProject.products.entity.Product;
import com.hii.finalProject.products.repository.ProductRepository;
import com.hii.finalProject.products.service.ProductServiceImpl;
import com.hii.finalProject.stock.dto.*;
import com.hii.finalProject.stock.entity.Stock;
import com.hii.finalProject.stock.repository.StockRepository;
import com.hii.finalProject.stockMutation.entity.MutationType;
import com.hii.finalProject.stockMutation.entity.StockMutation;
import com.hii.finalProject.stockMutation.entity.StockMutationStatus;
import com.hii.finalProject.stockMutation.repository.StockMutationRepository;
import com.hii.finalProject.stockMutationJournal.entity.StockMutationJournal;
import com.hii.finalProject.stockMutationJournal.repository.StockMutationJournalRepository;
import com.hii.finalProject.users.entity.User;
import com.hii.finalProject.users.repository.UserRepository;
import com.hii.finalProject.warehouse.entity.Warehouse;
import com.hii.finalProject.warehouse.repository.WarehouseRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;


@Service
public class StockServiceImpl implements StockService{
    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final ProductServiceImpl productService;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;
    private final StockMutationRepository stockMutationRepository;
    private final StockMutationJournalRepository stockMutationJournalRepository;

    public StockServiceImpl(StockRepository stockRepository, ProductRepository productRepository, ProductServiceImpl productService, WarehouseRepository warehouseRepository, UserRepository userRepository, StockMutationRepository stockMutationRepository, StockMutationJournalRepository stockMutationJournalRepository){
        this.stockRepository = stockRepository;
        this.productService = productService;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.userRepository = userRepository;
        this.stockMutationRepository = stockMutationRepository;
        this.stockMutationJournalRepository = stockMutationJournalRepository;
    }

    @Override
    public StockDtoResponse createStock(StockDtoRequest stockDtoRequest, String email) {
        Product product = productRepository.findById(stockDtoRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Warehouse warehouse = warehouseRepository.findById(stockDtoRequest.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        Stock existingStock = stockRepository.findByProductAndWarehouse(product, warehouse).orElse(null);

        Stock stock;
        int quantityChange;

        if (existingStock != null) {
            quantityChange = stockDtoRequest.getQuantity() - existingStock.getQuantity();
            existingStock.setQuantity(stockDtoRequest.getQuantity());
            stock = stockRepository.save(existingStock);
        } else {
            stock = new Stock();
            stock.setProduct(product);
            stock.setWarehouse(warehouse);
            stock.setQuantity(stockDtoRequest.getQuantity());
            quantityChange = stockDtoRequest.getQuantity();
            stock = stockRepository.save(stock);
        }
        StockMutation stockMutation = createStockMutation(product, warehouse, Math.abs(quantityChange));
        StockMutation savedMutation = stockMutationRepository.save(stockMutation);
        StockMutationJournal journal = createStockMutationJournal(savedMutation, warehouse, true); // Always IN for create/update
        stockMutationJournalRepository.save(journal);

        return convertToDto(stock, null);
    }

    @Override
    public Page<StockDtoResponse> getAllStock(
            String search,
            Long productId,
            Long warehouseId,
            String categoryName,
            Integer minQuantity,
            Integer maxQuantity,
            Double minPrice,
            Double maxPrice,
            String sortBy,
            String sortDirection,
            Pageable pageable,
            String email
    ) {
        Specification<Stock> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (search != null && !search.isEmpty()) {
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("product").get("name")), "%" + search.toLowerCase() + "%"),
                        cb.like(cb.lower(root.get("warehouse").get("name")), "%" + search.toLowerCase() + "%")
                ));
            }
            if (root.get("product") != null && productId != null) {
                predicates.add(cb.equal(root.get("product").get("id"), productId));
            }

            if (warehouseId != null) {
                predicates.add(cb.equal(root.get("warehouse").get("id"), warehouseId));
            }

            if (categoryName != null && !categoryName.isEmpty()) {
                String[] categories = categoryName.split(",");
                List<Predicate> categoryPredicates = new ArrayList<>();
                for (String category : categories) {
                    categoryPredicates.add(cb.equal(cb.lower(root.get("product").get("category").get("name")), category.trim().toLowerCase()));
                }
                predicates.add(cb.or(categoryPredicates.toArray(new Predicate[0])));
            }

            if (minQuantity != null) {
                predicates.add(cb.ge(root.get("quantity"), minQuantity));
            }
            if (maxQuantity != null) {
                predicates.add(cb.le(root.get("quantity"), maxQuantity));
            }
            if (minPrice != null) {
                predicates.add(cb.ge(root.get("product").get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.le(root.get("product").get("price"), maxPrice));
            }

            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };
        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<Stock> stocks = stockRepository.findAll(spec, pageableWithSort);
        if (stocks.getTotalElements() == 0 && pageable.getPageNumber() > 0) {
            throw new IllegalArgumentException("Page number exceeds total available pages");
        }
        return stocks.map(stock -> convertToDto(stock, email));
    }


    @Override
    public void deleteStock(Long id) {
        stockRepository.deleteById(id);
    }

    @Override
    public StockDtoResponse updateStock(Long id, StockDtoRequest stockDtoRequest, String email) {
        Stock existingStock = stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        Product product = productRepository.findById(stockDtoRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Warehouse warehouse = warehouseRepository.findById(stockDtoRequest.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        int oldQuantity = existingStock.getQuantity();
        int newQuantity = stockDtoRequest.getQuantity();
        int quantityDifference = newQuantity - oldQuantity;

        existingStock.setProduct(product);
        existingStock.setWarehouse(warehouse);
        existingStock.setQuantity(stockDtoRequest.getQuantity());

        Stock updatedStock = stockRepository.save(existingStock);
        if (quantityDifference != 0) {
            StockMutation stockMutation = createStockMutation(product, warehouse, Math.abs(quantityDifference));
            StockMutation savedMutation = stockMutationRepository.save(stockMutation);

            StockMutationJournal journal = createStockMutationJournal(savedMutation, warehouse, quantityDifference > 0);
            stockMutationJournalRepository.save(journal);
        }
        return convertToDto(updatedStock, null);
    }

    public StockDtoResponse convertToDto(Stock stock, String email) {
        StockDtoResponse responseDto = new StockDtoResponse();
        responseDto.setId(stock.getId());
        responseDto.setProductId(stock.getProduct().getId());
        responseDto.setProductName(stock.getProduct().getName());
        responseDto.setWarehouseId(stock.getWarehouse().getId());
        responseDto.setWarehouseName(stock.getWarehouse().getName());
        responseDto.setQuantity(stock.getQuantity());
        if (stock.getProduct().getCategories() != null) {
            responseDto.setCategoryId(stock.getProduct().getCategories().getId());
            responseDto.setCategoryName(stock.getProduct().getCategories().getName());
        }
        if (stock.getProduct().getProductImages() != null && !stock.getProduct().getProductImages().isEmpty()) {
            String imageUrl = stock.getProduct().getProductImages().get(0).getImageUrl();
            responseDto.setProductImageUrl(imageUrl);
        } else {
            responseDto.setProductImageUrl(null);
        }
        if (email != null) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new DataNotFoundException("User not found"));

            if (user.getWarehouse() != null && user.getWarehouse().getId() != null) {
                responseDto.setLoginWarehouseId(user.getWarehouse().getId());
            }
        }
        responseDto.setPrice(stock.getProduct().getPrice());
        responseDto.setWeight(stock.getProduct().getWeight());
        responseDto.setCreatedAt(stock.getCreatedAt());
        responseDto.setUpdatedAt(stock.getUpdatedAt());
        return responseDto;
    }
    //manualUpdate
    public static StockDtoProductResponse convertFromStock(Stock stock) {
        StockDtoProductResponse responseDto = new StockDtoProductResponse();
        responseDto.setId(stock.getId());
        responseDto.setWarehouseId(stock.getWarehouse().getId());
        responseDto.setWarehouseName(stock.getWarehouse().getName());
        responseDto.setQuantity(stock.getQuantity());
        return responseDto;
    }
    //update journal
    private StockMutation createStockMutation(Product product, Warehouse warehouse, int quantity) {
        StockMutation stockMutation = new StockMutation();
        stockMutation.setProduct(product);
        stockMutation.setOrigin(warehouse);
        stockMutation.setDestination(warehouse);
        stockMutation.setQuantity(quantity);
        stockMutation.setMutationType(MutationType.MANUAL);
        stockMutation.setStatus(StockMutationStatus.COMPLETED);
        stockMutation.setCreatedAt(LocalDateTime.now());
        stockMutation.setUpdatedAt(LocalDateTime.now());
        stockMutation.setCompletedAt(LocalDateTime.now());
        return stockMutation;
    }

    private StockMutationJournal createStockMutationJournal(StockMutation stockMutation, Warehouse warehouse, boolean isStockIncrease) {
        StockMutationJournal journal = new StockMutationJournal();
        journal.setStockMutation(stockMutation);
        journal.setWarehouse(warehouse);
        journal.setCreatedAt(LocalDateTime.now());
        journal.setMutationType(isStockIncrease ? StockMutationJournal.MutationType.IN : StockMutationJournal.MutationType.OUT);
        return journal;
    }
    //stockreport
}
