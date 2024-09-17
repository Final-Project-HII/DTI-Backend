package com.hii.finalProject.warehouse.service;


import com.hii.finalProject.warehouse.dto.WarehouseDTO;
import com.hii.finalProject.warehouse.dto.WarehouseDetailResponseDto;
import com.hii.finalProject.warehouse.entity.Warehouse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface WarehouseService {
    Page<Warehouse> getAllWarehouses(String name, String cityName, int page, int size);
    Warehouse getWarehouseById(Long id);
    Warehouse createWarehouse(WarehouseDTO data);
    Warehouse updateWarehouse(Long id, WarehouseDTO data);
    void deleteWarehouse(Long id);
    Warehouse findNearestWarehouse(String email);
}