package com.hii.finalProject.stockMutation.service;

import com.hii.finalProject.exceptions.DataNotFoundException;
import com.hii.finalProject.products.entity.Product;
import com.hii.finalProject.products.repository.ProductRepository;
import com.hii.finalProject.stock.entity.Stock;
import com.hii.finalProject.stock.repository.StockRepository;
import com.hii.finalProject.stockMutation.dto.*;
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
import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;


@Service
@Transactional
public class StockMutationServiceImpl implements StockMutationService {

    private final StockMutationRepository stockMutationRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockMutationJournalRepository stockMutationJournalRepository;

    public StockMutationServiceImpl(StockMutationRepository stockMutationRepository, UserRepository userRepository, ProductRepository productRepository, StockRepository stockRepository,
                                    WarehouseRepository warehouseRepository, StockMutationJournalRepository stockMutationJournalRepository) {
        this.stockMutationRepository = stockMutationRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;
        this.warehouseRepository = warehouseRepository;
        this.stockMutationJournalRepository = stockMutationJournalRepository;
    }

    @Override
    public StockMutationResponseDto createManualMutation(StockMutationRequestDto request, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Product not found"));
        Warehouse origin = warehouseRepository.findById(request.getOriginWarehouseId())
                .orElseThrow(() -> new DataNotFoundException("Origin warehouse not found"));
        Warehouse destination = warehouseRepository.findById(request.getDestinationWarehouseId())
                .orElseThrow(() -> new DataNotFoundException("Origin warehouse not found"));
        Stock originStock = stockRepository.findByProductAndWarehouse(product, origin)
                .orElseThrow(() -> new DataNotFoundException("Stock not found in origin warehouse"));

        if (originStock.getQuantity() < request.getQuantity()) {
            throw new InsufficientStockException("Not enough stock in origin warehouse");
        }

        StockMutation mutation = new StockMutation();
        mutation.setProduct(product);
        mutation.setOrigin(origin);
        mutation.setDestination(destination);
        mutation.setQuantity(request.getQuantity());
        mutation.setMutationType(MutationType.MANUAL);
        mutation.setStatus(StockMutationStatus.REQUESTED);
        mutation.setRequestedBy(user.getId().intValue());
        mutation.setCreatedAt(LocalDateTime.now());

        StockMutation savedMutation = stockMutationRepository.save(mutation);
        return convertToResponseDto(savedMutation, username);
    }

    public StockMutationResponseDto convertToResponseDto(StockMutation mutation, String email) {
        StockMutationResponseDto dto = new StockMutationResponseDto();
        dto.setId(mutation.getId());
        dto.setProductId(mutation.getProduct().getId());
        dto.setProductName(mutation.getProduct().getName());
        if (mutation.getProduct().getProductImages() != null && !mutation.getProduct().getProductImages().isEmpty()) {
            String imageUrl = mutation.getProduct().getProductImages().get(0).getImageUrl();
            dto.setProductImageUrl(imageUrl);
        } else {
            dto.setProductImageUrl(null);
        }
        dto.setDestinationWarehouseName(mutation.getDestination().getName());
        dto.setOriginWarehouseName(mutation.getOrigin().getName());
        dto.setDestinationWarehouseId(mutation.getDestination().getId());
        dto.setOriginWarehouseId(mutation.getOrigin().getId());
        dto.setQuantity(mutation.getQuantity());
        dto.setStatus(mutation.getStatus());
        dto.setMutationType(mutation.getMutationType());
        dto.setRemarks(mutation.getRemarks());
        dto.setRequestedBy(getNameById(mutation.getRequestedBy()));
        dto.setHandledBy(getNameById(mutation.getHandledBy()));
        dto.setCreatedAt(mutation.getCreatedAt());
        dto.setUpdatedAt(mutation.getUpdatedAt());
        if (email != null) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new DataNotFoundException("User not found"));
            if (user.getWarehouse().getId() != null) {
                dto.setLoginWarehouseId(user.getWarehouse().getId());

            }
        }
        return dto;
    }
    private Integer getUserIdByUsername(String username) {
        return userRepository.findByEmail(username)
                .map(user -> user.getId().intValue())
                .orElseThrow(() -> new DataNotFoundException("User not found with email: " + username));
    }
@Override
public StockMutationResponseDto processMutation(StockMutationProcessDto processDto, String handledBy) {
    StockMutation mutation = stockMutationRepository.findById(processDto.getId())
            .orElseThrow(() -> new DataNotFoundException("Stock mutation not found"));

    if (mutation.getStatus() == processDto.getStatus()) {
        throw new IllegalStateException("New status must be different from the current status");
    }
    validateStatusTransition(mutation.getStatus(), processDto.getStatus());

    StockMutationStatus oldStatus = mutation.getStatus();
    mutation.setStatus(processDto.getStatus());
    mutation.setRemarks(processDto.getRemarks());
    mutation.setHandledBy(getUserIdByUsername(handledBy));
    mutation.setUpdatedAt(LocalDateTime.now());

    switch (processDto.getStatus()) {
        case APPROVED, IN_TRANSIT:
            break;
        case COMPLETED:
            updateStock(mutation);
            createStockMutationJournal(mutation, mutation.getOrigin(), StockMutationJournal.MutationType.OUT);
            createStockMutationJournal(mutation, mutation.getDestination(), StockMutationJournal.MutationType.IN);
            mutation.setCompletedAt(LocalDateTime.now());
            break;
        case CANCELLED:
            break;
        default:
            throw new IllegalArgumentException("Unsupported status: " + processDto.getStatus());
    }

    StockMutation updatedMutation = stockMutationRepository.save(mutation);
    return convertToResponseDto(updatedMutation, handledBy);
}
    private void createStockMutationJournal(StockMutation mutation, Warehouse warehouse, StockMutationJournal.MutationType mutationType) {
        StockMutationJournal journal = new StockMutationJournal();
        journal.setStockMutation(mutation);
        journal.setWarehouse(warehouse);
        journal.setMutationType(mutationType);
        journal.setCreatedAt(LocalDateTime.now());

        stockMutationJournalRepository.save(journal);
    }

    private void validateStatusTransition(StockMutationStatus currentStatus, StockMutationStatus newStatus) {
        switch (currentStatus) {
            case REQUESTED:
                if (newStatus != StockMutationStatus.APPROVED && newStatus != StockMutationStatus.CANCELLED) {
                    throw new IllegalStateException("REQUESTED mutations can only be APPROVED or CANCELLED");
                }
                break;
            case APPROVED:
                if (newStatus != StockMutationStatus.IN_TRANSIT && newStatus != StockMutationStatus.CANCELLED) {
                    throw new IllegalStateException("APPROVED mutations can only move to IN_TRANSIT or be CANCELLED");
                }
                break;
            case IN_TRANSIT:
                if (newStatus != StockMutationStatus.COMPLETED && newStatus != StockMutationStatus.CANCELLED) {
                    throw new IllegalStateException("IN_TRANSIT mutations can only be COMPLETED or CANCELLED");
                }
                break;
            case COMPLETED:
            case CANCELLED:
                throw new IllegalStateException("COMPLETED or CANCELLED mutations cannot change status");
            default:
                throw new IllegalArgumentException("Unsupported current status: " + currentStatus);
        }
    }

    private void updateStock(StockMutation mutation) {
        Stock originStock = stockRepository.findByProductAndWarehouse(mutation.getProduct(), mutation.getOrigin())
                .orElseThrow(() -> new DataNotFoundException("Stock not found in origin warehouse"));

        if (originStock.getQuantity() < mutation.getQuantity()) {
            throw new InsufficientStockException("Not enough stock in origin warehouse");
        }

        originStock.setQuantity(originStock.getQuantity() - mutation.getQuantity());
        stockRepository.save(originStock);

        Stock destinationStock = stockRepository.findByProductAndWarehouse(mutation.getProduct(), mutation.getDestination())
                .orElseGet(() -> {
                    Stock newStock = new Stock();
                    newStock.setProduct(mutation.getProduct());
                    newStock.setWarehouse(mutation.getDestination());
                    newStock.setQuantity(0);
                    return newStock;
                });

        destinationStock.setQuantity(destinationStock.getQuantity() + mutation.getQuantity());
        stockRepository.save(destinationStock);
    }

    public static class InsufficientStockException extends RuntimeException {
        public InsufficientStockException(String message) {
            super(message);
        }
    }

    @Override
    public StockMutationResponseDto getMutationById(Long mutationId) {
        StockMutation mutation = stockMutationRepository.findById(mutationId)
                .orElseThrow(() -> new DataNotFoundException("Stock mutation not found"));
        return convertToResponseDto(mutation, null);
    }

    @Override
    public List<StockMutationResponseDto> getMutationByUser(String userEmail) {
        return null;
    }

    @Override
    public Page<StockMutationResponseDto> getAllStockMutations(
            String email,
            Long originWarehouseId,
            Long destinationWarehouseId,
            String productName,
            StockMutationStatus status,
            LocalDateTime createdAtStart,
            LocalDateTime createdAtEnd,
            LocalDateTime updatedAtStart,
            LocalDateTime updatedAtEnd,
            String sortBy,
            String sortDirection,
            Pageable pageable
    ) {
        Specification<StockMutation> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (originWarehouseId != null) {predicates.add(cb.equal(root.get("origin").get("id"), originWarehouseId));}
            if (destinationWarehouseId != null) {predicates.add(cb.equal(root.get("destination").get("id"), destinationWarehouseId));}
            if (productName != null && !productName.isEmpty()) {predicates.add(cb.like(cb.lower(root.get("product").get("name")), "%" + productName.toLowerCase() + "%"));}
            if (status != null){
                predicates.add(cb.equal(root.get("StockMutationStatus"), status));
            }
            if (createdAtStart != null) {predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), createdAtStart));}
            if (createdAtEnd != null) {predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), createdAtEnd));}
            if (updatedAtStart != null) {predicates.add(cb.greaterThanOrEqualTo(root.get("updatedAt"), updatedAtStart));}
            if (updatedAtEnd != null) {predicates.add(cb.lessThanOrEqualTo(root.get("updatedAt"), updatedAtEnd));}
            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<StockMutation> mutations = stockMutationRepository.findAll(spec, pageableWithSort);
        return mutations.map(mutation -> convertToResponseDto(mutation, email));
    }

    private String getNameById(Integer userId) {
        if (userId == null) return null;
        return userRepository.findById(userId.longValue())
                .map(user -> user.getName())
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + userId));
    }
    @Override
    public Page<StockMutationJournalDto> getStockMutationJournals(
            Long warehouseId,
            String productName,
            StockMutationJournal.MutationType mutationType,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String email,
            Pageable pageable
    ) {
        Specification<StockMutationJournal> spec = (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (warehouseId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("warehouse").get("id"), warehouseId));
            }
            if (productName != null && !productName.isEmpty()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("stockMutation").get("product").get("name")), "%" + productName.toLowerCase() + "%"));
            }
            if (mutationType != null) {
                predicate = cb.and(predicate, cb.equal(root.get("mutationType"), mutationType));
            }
            if (startDate != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }
            if (endDate != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }

            return predicate;
        };

        Page<StockMutationJournal> journalPage = stockMutationJournalRepository.findAll(spec, pageable);
        return journalPage.map(journal -> fromEntity(journal, email));
    }




    public StockMutationJournalDto fromEntity(StockMutationJournal journal, String email) {
        StockMutationJournalDto dto = new StockMutationJournalDto();
        long generatedId = StockMutationJurnalIdGenerator.generateId();
        dto.setUUID(generatedId);
        dto.setId(journal.getId());
        dto.setStockMutationId(journal.getStockMutation().getId());
        dto.setProductName(journal.getStockMutation().getProduct().getName());
        dto.setWarehouseName(journal.getWarehouse().getName());
        if (journal.getMutationType() == StockMutationJournal.MutationType.IN) {
            dto.setAnotherWarehouse(journal.getStockMutation().getOrigin().getName());
        } else {
            dto.setAnotherWarehouse(journal.getStockMutation().getDestination().getName());
        }
        Stock stock = journal.getWarehouse().getStocks().stream()
                .filter(s -> s.getProduct().equals(journal.getStockMutation().getProduct()))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException("Stock not found"));

        if (journal.getMutationType() == StockMutationJournal.MutationType.IN) {
            dto.setBeginningStock(stock.getQuantity() - journal.getStockMutation().getQuantity());
            dto.setEndingStock(stock.getQuantity());
        } else {
            dto.setBeginningStock(stock.getQuantity() + journal.getStockMutation().getQuantity());
            dto.setEndingStock(stock.getQuantity());
        }
        if (email != null) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new DataNotFoundException("User not found"));

            if (user.getWarehouse() != null && user.getWarehouse().getId() != null) {
                dto.setLoginWarehouseId(user.getWarehouse().getId());
            }
        }
        dto.setMutationType(journal.getMutationType());
        dto.setQuantity(journal.getStockMutation().getQuantity());
        dto.setCreatedAt(journal.getCreatedAt());
        return dto;
    }
    public class StockMutationJurnalIdGenerator {
        private static final AtomicLong counter = new AtomicLong(0L);
        private static final Random random = new Random();
        public static long generateId() {
            long timestamp = Instant.now().toEpochMilli();
            long sequence = counter.incrementAndGet();
            long randomPart = random.nextInt(9999);
            long generatedId = (timestamp << 16) | (sequence & 0xFFFF) | (randomPart & 0xFFFF);
            return generatedId;
        }
        public static void main(String[] args) {
            long newId = StockMutationJurnalIdGenerator.generateId();
        }
    }
}