package com.hii.finalProject.stock.service;

import com.hii.finalProject.products.entity.Product;
import com.hii.finalProject.products.repository.ProductRepository;
import com.hii.finalProject.stock.dto.StockDtoRequest;
import com.hii.finalProject.stock.dto.StockDtoResponse;
import com.hii.finalProject.stock.entity.Stock;
import com.hii.finalProject.stock.repository.StockRepository;
import com.hii.finalProject.users.entity.User;
import com.hii.finalProject.users.repository.UserRepository;
import com.hii.finalProject.warehouse.entity.Warehouse;
import com.hii.finalProject.warehouse.repository.WarehouseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockServiceImpl implements StockService{
    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;

    public StockServiceImpl(StockRepository stockRepository, ProductRepository productRepository, WarehouseRepository warehouseRepository, UserRepository userRepository){
        this.stockRepository = stockRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.userRepository = userRepository;
    }

    @Override
    public StockDtoResponse createStock(StockDtoRequest stockDtoRequest) {
        Product product = productRepository.findById(stockDtoRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Warehouse warehouse = warehouseRepository.findById(stockDtoRequest.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        Stock stock = new Stock();
        stock.setProduct(product);
        stock.setWarehouse(warehouse);
        stock.setQuantity(stockDtoRequest.getQuantity());

        Stock savedStock = stockRepository.save(stock);
        return convertToDto(savedStock);
    }

//    @Override
//    public List<Stock> findByWarehouse(String email) {
//        Warehouse warehouse = warehouseRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
//        return stockRepository.findByWarehouse(warehouse);
//    }

    @Override
    public List<StockDtoResponse> getAllStock() {
        return stockRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteStock(Long id) {
        stockRepository.deleteById(id);
    }

    @Override
    public StockDtoResponse updateStock(Long id, StockDtoRequest stockDtoRequest) {
        Stock existingStock = stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        Product product = productRepository.findById(stockDtoRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Warehouse warehouse = warehouseRepository.findById(stockDtoRequest.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        existingStock.setProduct(product);
        existingStock.setWarehouse(warehouse);
        existingStock.setQuantity(stockDtoRequest.getQuantity());

        Stock updatedStock = stockRepository.save(existingStock);
        return convertToDto(updatedStock);
    }
    public StockDtoResponse convertToDto(Stock stock) {
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
        return responseDto;
    }
    //manualUpdate

}
