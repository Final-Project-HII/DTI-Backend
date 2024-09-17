package com.hii.finalProject.stockMutation.service;

import com.hii.finalProject.exceptions.DataNotFoundException;
import com.hii.finalProject.products.entity.Product;
import com.hii.finalProject.products.repository.ProductRepository;
import com.hii.finalProject.products.service.ProductService;
import com.hii.finalProject.stock.entity.Stock;
import com.hii.finalProject.stock.repository.StockRepository;
import com.hii.finalProject.stock.service.StockService;
import com.hii.finalProject.stockMutation.dto.StockMutationProcessDto;
import com.hii.finalProject.stockMutation.dto.StockMutationRequestDto;
import com.hii.finalProject.stockMutation.dto.StockMutationResponseDto;
import com.hii.finalProject.stockMutation.entity.MutationType;
import com.hii.finalProject.stockMutation.entity.StockMutation;
import com.hii.finalProject.stockMutation.entity.StockMutationStatus;
import com.hii.finalProject.stockMutation.repository.StockMutationRepository;
import com.hii.finalProject.users.entity.User;
import com.hii.finalProject.users.repository.UserRepository;
import com.hii.finalProject.users.service.UserService;
import com.hii.finalProject.warehouse.entity.Warehouse;
import com.hii.finalProject.warehouse.repository.WarehouseRepository;
import com.hii.finalProject.warehouse.service.WarehouseService;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class StockMutationServiceImpl implements StockMutationService {

    private final StockMutationRepository stockMutationRepository;
    private final StockService stockService;
    private final UserService userService;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final WarehouseService warehouseService;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final WarehouseRepository warehouseRepository;
    private final DistanceService distanceService;

    public StockMutationServiceImpl(StockMutationRepository stockMutationRepository, StockService stockService,
                                    UserService userService, UserRepository userRepository,
                                    ProductService productService, ProductRepository productRepository,
                                    WarehouseService warehouseService, StockRepository stockRepository,
                                    WarehouseRepository warehouseRepository, DistanceService distanceService) {
        this.stockMutationRepository = stockMutationRepository;
        this.stockService = stockService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.productService = productService;
        this.productRepository = productRepository;
        this.warehouseService = warehouseService;
        this.stockRepository = stockRepository;
        this.warehouseRepository = warehouseRepository;
        this.distanceService = distanceService;
    }

    @Override
    public StockMutationResponseDto createManualMutation(StockMutationRequestDto request, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Product not found"));

        // Origin warehouse (asal produk)
        Warehouse origin = warehouseRepository.findById(request.getOriginWarehouseId())
                .orElseThrow(() -> new DataNotFoundException("Origin warehouse not found"));
        Warehouse destination = warehouseRepository.findById(request.getDestinationWarehouseId())
                .orElseThrow(() -> new DataNotFoundException("Origin warehouse not found"));

        // Destination warehouse (tujuan produk)
//        Warehouse destination;
//        if (user.getWarehouseId() != null) {
//            destination = warehouseRepository.findById(user.getWarehouseId().longValue())
//                    .orElseThrow(() -> new DataNotFoundException("User's warehouse not found"));
//        } else {
//            destination = warehouseRepository.findById(request.getDestinationWarehouseId())
//                    .orElseThrow(() -> new DataNotFoundException("Destination warehouse not found"));
//        }

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
        // Pastikan productImages tidak null atau kosong
        if (mutation.getProduct().getProductImages() != null && !mutation.getProduct().getProductImages().isEmpty()) {
            String imageUrl = mutation.getProduct().getProductImages().get(0).getImageUrl();
            dto.setProductImageUrl(imageUrl);
        } else {
            dto.setProductImageUrl(null);
        }

        // Set warehouse names
        dto.setDestinationWarehouseName(mutation.getDestination().getName());
        dto.setOriginWarehouseName(mutation.getOrigin().getName());

//        User user = null;
//        if (userEmail != null) {
//            user = userRepository.findByEmail(userEmail)
//                    .orElseThrow(() -> new DataNotFoundException("User not found"));
//        }
        dto.setDestinationWarehouseId(mutation.getDestination().getId());
        dto.setOriginWarehouseId(mutation.getOrigin().getId());
        dto.setQuantity(mutation.getQuantity());
        dto.setStatus(mutation.getStatus());
        dto.setMutationType(mutation.getMutationType());
        dto.setRemarks(mutation.getRemarks());
//        dto.setRequestedBy(getUsernameById(mutation.getRequestedBy()));
//        dto.setHandledBy(getUsernameById(mutation.getHandledBy()));
        dto.setRequestedBy(getNameById(mutation.getRequestedBy()));
        dto.setHandledBy(getNameById(mutation.getHandledBy()));
        dto.setCreatedAt(mutation.getCreatedAt());
        dto.setUpdatedAt(mutation.getUpdatedAt());
        // Set login warehouse ID
        if (email != null) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new DataNotFoundException("User not found"));
            if (user.getWarehouseId() != null) {
                dto.setLoginWarehouseId(user.getWarehouseId().longValue());
            }
        }

        // Logika hanya untuk mutasi baru
//        if (mutation.getStatus() == StockMutationStatus.REQUESTED && userEmail != null) {
//            User user = userRepository.findByEmail(userEmail)
//                    .orElseThrow(() -> new DataNotFoundException("User not found"));
//            if (user.getWarehouseId() != null) {
//                dto.setDestinationWarehouseId(user.getWarehouseId().longValue());
//            }
//        }
        return dto;

    }
//    private ProductImageResponseDto convertToImageResponseDto(ProductImage image) {
//        ProductImageResponseDto imageDto = new ProductImageResponseDto();
//        imageDto.setId(image.getId());
//        imageDto.setImageUrl(image.getImageUrl()); // Asumsi ada properti url dalam ProductImage
//        // Tambahkan properti lain yang perlu disalin dari ProductImage ke ProductImageResponseDto
//        return imageDto;
//    }

    private Integer getUserIdByUsername(String username) {
        return userRepository.findByEmail(username)
                .map(user -> user.getId().intValue())
                .orElseThrow(() -> new DataNotFoundException("User not found with email: " + username));
    }

    private String getUsernameById(Integer userId) {
        if (userId == null) return null;
        return userRepository.findById(userId.longValue())
                .map(User::getEmail)
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + userId));
    }

    @Override
    public StockMutationResponseDto processMutation(StockMutationProcessDto processDto, String handledBy) {
        StockMutation mutation = stockMutationRepository.findById(processDto.getId())
                .orElseThrow(() -> new DataNotFoundException("Stock mutation not found"));

        if (mutation.getStatus() != StockMutationStatus.REQUESTED) {
            throw new IllegalStateException("Invalid mutation status for processing");
        }

        mutation.setStatus(processDto.getStatus());
        mutation.setRemarks(processDto.getRemarks());
        mutation.setHandledBy(getUserIdByUsername(handledBy));
        mutation.setUpdatedAt(LocalDateTime.now());

        if (processDto.getStatus() == StockMutationStatus.COMPLETED) {
            updateStock(mutation);
        }

        StockMutation updatedMutation = stockMutationRepository.save(mutation);
        return convertToResponseDto(updatedMutation, handledBy);
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
            String status,
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

            if (originWarehouseId != null) {
                predicates.add(cb.equal(root.get("origin").get("id"), originWarehouseId));
            }

            if (destinationWarehouseId != null) {
                predicates.add(cb.equal(root.get("destination").get("id"), destinationWarehouseId));
            }

            if (productName != null && !productName.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("product").get("name")), "%" + productName.toLowerCase() + "%"));
            }

            if (status != null && !status.isEmpty()) {
                try {
                    StockMutationStatus statusEnum = StockMutationStatus.valueOf(status.toUpperCase());
                    predicates.add(cb.equal(root.get("status"), statusEnum));
                } catch (IllegalArgumentException e) {
                    // Handle invalid status input
                    throw new IllegalArgumentException("Invalid status: " + status);
                }
            }

            if (createdAtStart != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), createdAtStart));
            }

            if (createdAtEnd != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), createdAtEnd));
            }

            if (updatedAtStart != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("updatedAt"), updatedAtStart));
            }

            if (updatedAtEnd != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("updatedAt"), updatedAtEnd));
            }

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
                .map(user -> user.getName()) // Assuming User has firstName and lastName fields
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + userId));
    }

}