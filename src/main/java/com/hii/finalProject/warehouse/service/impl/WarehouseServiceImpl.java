package com.hii.finalProject.warehouse.service.impl;

import com.hii.finalProject.city.entity.City;
import com.hii.finalProject.exceptions.DataNotFoundException;
import com.hii.finalProject.products.entity.Product;
import com.hii.finalProject.stock.dto.StockDtoResponse;
import com.hii.finalProject.stock.dto.StockDtoWarehouseResponse;
import com.hii.finalProject.stock.entity.Stock;
import com.hii.finalProject.stock.service.StockServiceImpl;
import com.hii.finalProject.warehouse.dto.WarehouseDTO;
import com.hii.finalProject.warehouse.dto.WarehouseDetailResponseDto;
import com.hii.finalProject.warehouse.entity.Warehouse;
import com.hii.finalProject.warehouse.repository.WarehouseRepository;
import com.hii.finalProject.warehouse.service.WarehouseService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final StockServiceImpl stockService;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository, StockServiceImpl stockService) {
        this.warehouseRepository = warehouseRepository;
        this.stockService = stockService;
    }

    @Override
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    @Override
    public Optional<Warehouse> getWarehouseById(Long id) {
        return warehouseRepository.findById(id);
    }

    @Override
    public Warehouse createWarehouse(WarehouseDTO data) {
        Warehouse newWarehouse = data.toEntity();
        return warehouseRepository.save(newWarehouse);
    }

    @Override
    public Warehouse updateWarehouse(Long id, WarehouseDTO data) {
        Warehouse existingWarehouse = warehouseRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Warehouse with ID " + id + " is not found"));
        existingWarehouse.setName(data.getName());
        existingWarehouse.setAddressLine(data.getAddressLine());
        City city = new City();
        city.setId(data.getCityId());
        existingWarehouse.setCityId(city);
        existingWarehouse.setPostalCode(data.getPostalCode());
        existingWarehouse.setLat(data.getLat());
        existingWarehouse.setLon(data.getLon());
        return warehouseRepository.save(existingWarehouse);
    }




    @Override
    public void deleteWarehouse(Long id) {
        warehouseRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Warehouse with ID " + id + " is not found"));
        warehouseRepository.deleteById(id);
    }
    @Override
    public WarehouseDetailResponseDto getWarehouseDetailById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Warehouse with ID " + id + " is not found"));
        List<Stock> stocks = warehouse.getStocks();
        return getWarehouseDetail(warehouse, stocks);
    }

    private WarehouseDetailResponseDto getWarehouseDetail(Warehouse warehouse, List<Stock> stocks) {
        WarehouseDetailResponseDto responseDto = new WarehouseDetailResponseDto();
        responseDto.setId(warehouse.getId());
        responseDto.setName(warehouse.getName());
        responseDto.setAddressLine(warehouse.getAddressLine());
        responseDto.setPostalCode(warehouse.getPostalCode());
        responseDto.setLat(warehouse.getLat());
        responseDto.setLon(warehouse.getLon());
        responseDto.setUpdatedAt(warehouse.getUpdatedAt());
        responseDto.setCreatedAt(warehouse.getCreatedAt());

        // Konversi Stock ke StockDtoResponse
        List<StockDtoWarehouseResponse> stockDtoResponses = stocks.stream()
                .map(this::convertStockToDto)  // Menggunakan metode konversi lokal
                .collect(Collectors.toList());
        responseDto.setStocks(stockDtoResponses);

        return responseDto;
    }

    private StockDtoWarehouseResponse convertStockToDto(Stock stock) {
        StockDtoWarehouseResponse responseDto = new StockDtoWarehouseResponse();
        responseDto.setId(stock.getId());
        responseDto.setProductId(stock.getProduct().getId());
        responseDto.setProductName(stock.getProduct().getName());
        responseDto.setQuantity(stock.getQuantity());

        if (stock.getProduct().getCategories() != null) {
            responseDto.setCategoryId(stock.getProduct().getCategories().getId());
            responseDto.setCategoryName(stock.getProduct().getCategories().getName());
        }

        return responseDto;
    }
    @Override
    public Optional<Warehouse> findById(Long warehouseId) {
        return warehouseRepository.findById(warehouseId);
    }

}