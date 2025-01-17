package com.hii.finalProject.warehouse.service.impl;

import com.hii.finalProject.address.entity.Address;
import com.hii.finalProject.address.service.AddressService;
import com.hii.finalProject.address.service.impl.AddressServiceImpl;
import com.hii.finalProject.address.specification.AddressListSpecification;
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
import com.hii.finalProject.warehouse.specification.WarehouseListSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Point;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final StockServiceImpl stockService;
    private final AddressService addressService;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository, StockServiceImpl stockService, AddressService addressService) {
        this.warehouseRepository = warehouseRepository;
        this.stockService = stockService;
        this.addressService = addressService;
    }

    public Page<Warehouse> getAllWarehouses(String name, String cityName, int page, Integer size) {
        Specification<Warehouse> specification = Specification.where(WarehouseListSpecification.byWarehouseName(name)
                .and(WarehouseListSpecification.byCity(cityName))
                .and(WarehouseListSpecification.notDeleted()));

        if (size == null) {
            List<Warehouse> allWarehouses = warehouseRepository.findAll(specification);
            return new PageImpl<>(allWarehouses);
        } else {
            // If size is provided, use pagination
            Pageable pageable = PageRequest.of(page, size);
            return warehouseRepository.findAll(specification, pageable);
        }

    }

    @Override
    public Warehouse getWarehouseById(Long id) {
        Warehouse data = warehouseRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Warehouse with id " + id + " is not found"));
        return data;
    }



    @Override

    public Warehouse findNearestWarehouse(String email) {
        Address address = addressService.getActiveUserAddress(email);
        System.out.println(address);
        return warehouseRepository.findNearestWarehouse(address.getLon(),address.getLat());
    }

    @Override
    public Warehouse createWarehouse(WarehouseDTO data) {
        Warehouse newWarehouse = data.toEntity();
        return warehouseRepository.save(newWarehouse);
    }

    @Override
    public Warehouse updateWarehouse(Long id, WarehouseDTO data) {
        System.out.println(data);
        Warehouse existingWarehouse = warehouseRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Warehouse with ID " + id + " is not found"));
        existingWarehouse.setName(data.getName());
        existingWarehouse.setAddressLine(data.getAddressLine());
        City city = new City();
        city.setId(data.getCityId());
        existingWarehouse.setCity(city);
        existingWarehouse.setPostalCode(data.getPostalCode());
        existingWarehouse.setLat(data.getLat());
        existingWarehouse.setLon(data.getLon());
        return warehouseRepository.save(existingWarehouse);
    }

    @Override
    public void deleteWarehouse(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Warehouse with ID " + id + " is not found"));
        warehouse.setDeletedAt(Instant.now());
        warehouseRepository.save(warehouse);
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