package com.hii.finalProject.warehouse.service;


import com.hii.finalProject.warehouse.dto.WarehouseDTO;
import com.hii.finalProject.warehouse.entity.Warehouse;

import java.util.List;
import java.util.Optional;

public interface WarehouseService {
    List<Warehouse> getAllWarehouses();
    Optional<Warehouse> getWarehouseById(Long id);
    Warehouse createWarehouse(WarehouseDTO data);
    Warehouse updateWarehouse(Long id, WarehouseDTO data);
    void deleteWarehouse(Long id);
}