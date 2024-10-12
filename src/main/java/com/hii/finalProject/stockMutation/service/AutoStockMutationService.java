//package com.hii.finalProject.stockMutation.service;
//
//import com.hii.finalProject.order.entity.Order;
//import com.hii.finalProject.orderItem.entity.OrderItem;
//import com.hii.finalProject.stock.entity.Stock;
//import com.hii.finalProject.stock.repository.StockRepository;
//import com.hii.finalProject.stockMutation.entity.MutationType;
//import com.hii.finalProject.stockMutation.entity.StockMutation;
//import com.hii.finalProject.stockMutation.entity.StockMutationStatus;
//import com.hii.finalProject.stockMutation.repository.StockMutationRepository;
//import com.hii.finalProject.warehouse.entity.Warehouse;
//import com.hii.finalProject.warehouse.repository.WarehouseRepository;
//import com.hii.finalProject.stockMutationJournal.entity.StockMutationJournal;
//import com.hii.finalProject.stockMutationJournal.repository.StockMutationJournalRepository;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//
//@Service
//public class AutoStockMutationService {
//
//    private final WarehouseRepository warehouseRepository;
//    private final StockRepository stockRepository;
//    private final StockMutationRepository stockMutationRepository;
//    private final StockMutationJournalRepository stockMutationJournalRepository;
//
//    public AutoStockMutationService(WarehouseRepository warehouseRepository,
//                                    StockRepository stockRepository,
//                                    StockMutationRepository stockMutationRepository,
//                                    StockMutationJournalRepository stockMutationJournalRepository) {
//        this.warehouseRepository = warehouseRepository;
//        this.stockRepository = stockRepository;
//        this.stockMutationRepository = stockMutationRepository;
//        this.stockMutationJournalRepository = stockMutationJournalRepository;
//    }
//
//    @Transactional
//    public void processOrderAndCreateMutationIfNeeded(Order order) {
//        Warehouse orderWarehouse = order.getWarehouse();
//
//        for (OrderItem item : order.getItems()) {
//            Stock stock = stockRepository.findByProductAndWarehouse(item.getProduct(), orderWarehouse)
//                    .orElseThrow(() -> new RuntimeException("Stock not found for product in the warehouse"));
//
//            if (stock.getQuantity() < item.getQuantity()) {
//                int requiredQuantity = item.getQuantity() - stock.getQuantity();
//                Warehouse nearestWarehouse = warehouseRepository.findNearestWarehouse(
//                        orderWarehouse.getLon(), orderWarehouse.getLat());
//
//                if (nearestWarehouse == null) {
//                    throw new RuntimeException("No nearby warehouse found with sufficient stock");
//                }
//
//                Stock nearestWarehouseStock = stockRepository.findByProductAndWarehouse(item.getProduct(), nearestWarehouse)
//                        .orElseThrow(() -> new RuntimeException("Stock not found in nearest warehouse"));
//
//                if (nearestWarehouseStock.getQuantity() < requiredQuantity) {
//                    throw new RuntimeException("Insufficient stock in nearest warehouse");
//                }
//
//                // Create and save automatic stock mutation
//                StockMutation mutation = new StockMutation();
//                mutation.setProduct(item.getProduct());
//                mutation.setOrigin(nearestWarehouse);
//                mutation.setDestination(orderWarehouse);
//                mutation.setQuantity(requiredQuantity);
//                mutation.setStatus(StockMutationStatus.COMPLETED); // Auto-approved
//                mutation.setMutationType(MutationType.AUTOMATIC);
//                mutation.setCreatedAt(LocalDateTime.now());
//                stockMutationRepository.save(mutation);
//
//                // Update stock in both warehouses
//                nearestWarehouseStock.setQuantity(nearestWarehouseStock.getQuantity() - requiredQuantity);
//                stock.setQuantity(stock.getQuantity() + requiredQuantity);
//                stockRepository.save(nearestWarehouseStock);
//                stockRepository.save(stock);
//
//                // Create stock mutation journal entries
//                createStockMutationJournal(mutation, nearestWarehouse, StockMutationJournal.MutationType.OUT);
//                createStockMutationJournal(mutation, orderWarehouse, StockMutationJournal.MutationType.IN);
//            }
//        }
//    }
//
//    private void createStockMutationJournal(StockMutation mutation, Warehouse warehouse, StockMutationJournal.MutationType type) {
//        StockMutationJournal journal = new StockMutationJournal();
//        journal.setStockMutation(mutation);
//        journal.setWarehouse(warehouse);
//        journal.setMutationType(type);
//        journal.setCreatedAt(LocalDateTime.now());
//        stockMutationJournalRepository.save(journal);
//    }
//}
package com.hii.finalProject.stockMutation.service;

import com.hii.finalProject.order.entity.Order;
import com.hii.finalProject.orderItem.entity.OrderItem;
import com.hii.finalProject.stock.entity.Stock;
import com.hii.finalProject.stock.repository.StockRepository;
import com.hii.finalProject.stockMutation.entity.MutationType;
import com.hii.finalProject.stockMutation.entity.StockMutation;
import com.hii.finalProject.stockMutation.entity.StockMutationStatus;
import com.hii.finalProject.stockMutation.repository.StockMutationRepository;
import com.hii.finalProject.warehouse.entity.Warehouse;
import com.hii.finalProject.warehouse.repository.WarehouseRepository;
import com.hii.finalProject.stockMutationJournal.entity.StockMutationJournal;
import com.hii.finalProject.stockMutationJournal.repository.StockMutationJournalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AutoStockMutationService {

    private final WarehouseRepository warehouseRepository;
    private final StockRepository stockRepository;
    private final StockMutationRepository stockMutationRepository;
    private final StockMutationJournalRepository stockMutationJournalRepository;

    public AutoStockMutationService(WarehouseRepository warehouseRepository,
                                    StockRepository stockRepository,
                                    StockMutationRepository stockMutationRepository,
                                    StockMutationJournalRepository stockMutationJournalRepository) {
        this.warehouseRepository = warehouseRepository;
        this.stockRepository = stockRepository;
        this.stockMutationRepository = stockMutationRepository;
        this.stockMutationJournalRepository = stockMutationJournalRepository;
    }

    @Transactional
    public void processOrderAndCreateMutationIfNeeded(Order order) {
        Warehouse orderWarehouse = order.getWarehouse();

        for (OrderItem item : order.getItems()) {
            Stock stock = stockRepository.findByProductAndWarehouse(item.getProduct(), orderWarehouse)
                    .orElseThrow(() -> new RuntimeException("Stock not found for product in the warehouse"));

            if (stock.getQuantity() < item.getQuantity()) {
                int requiredQuantity = item.getQuantity() - stock.getQuantity();

                Warehouse sourceWarehouse = findWarehouseWithSufficientStock(item.getProduct().getId(), requiredQuantity, orderWarehouse);

                if (sourceWarehouse == null) {
                    throw new RuntimeException("No warehouse found with sufficient stock");
                }

                Stock sourceWarehouseStock = stockRepository.findByProductAndWarehouse(item.getProduct(), sourceWarehouse)
                        .orElseThrow(() -> new RuntimeException("Stock not found in source warehouse"));

                // Create and save automatic stock mutation
                StockMutation mutation = new StockMutation();
                mutation.setProduct(item.getProduct());
                mutation.setOrigin(sourceWarehouse);
                mutation.setDestination(orderWarehouse);
                mutation.setQuantity(requiredQuantity);
                mutation.setStatus(StockMutationStatus.COMPLETED); // Auto-approved
                mutation.setMutationType(MutationType.AUTOMATIC);
                mutation.setCreatedAt(LocalDateTime.now());
                stockMutationRepository.save(mutation);

                // Update stock in both warehouses
                sourceWarehouseStock.setQuantity(sourceWarehouseStock.getQuantity() - requiredQuantity);
                stock.setQuantity(stock.getQuantity() + requiredQuantity);
                stockRepository.save(sourceWarehouseStock);
                stockRepository.save(stock);

                // Create stock mutation journal entries
                createStockMutationJournal(mutation, sourceWarehouse, StockMutationJournal.MutationType.OUT);
                createStockMutationJournal(mutation, orderWarehouse, StockMutationJournal.MutationType.IN);
            }
        }
    }

    private Warehouse findWarehouseWithSufficientStock(Long productId, int requiredQuantity, Warehouse orderWarehouse) {
        List<Warehouse> warehouses = warehouseRepository.findAllOrderByDistance(orderWarehouse.getLon(), orderWarehouse.getLat());

        for (Warehouse warehouse : warehouses) {
            if (warehouse.getId().equals(orderWarehouse.getId())) {
                continue; // Skip the order's warehouse
            }

            Stock stock = stockRepository.findByProduct_IdAndWarehouse(productId, warehouse);
            if (stock != null && stock.getQuantity() >= requiredQuantity) {
                return warehouse;
            }
        }

        return null; // If no warehouse with sufficient stock is found
    }

    private void createStockMutationJournal(StockMutation mutation, Warehouse warehouse, StockMutationJournal.MutationType type) {
        StockMutationJournal journal = new StockMutationJournal();
        journal.setStockMutation(mutation);
        journal.setWarehouse(warehouse);
        journal.setMutationType(type);
        journal.setCreatedAt(LocalDateTime.now());
        stockMutationJournalRepository.save(journal);
    }
}