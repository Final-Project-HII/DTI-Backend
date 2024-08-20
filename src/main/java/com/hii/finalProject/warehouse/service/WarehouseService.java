package com.hii.finalProject.warehouse.service;


import com.hii.finalProject.warehouse.entity.Warehouse;

import java.util.List;
import java.util.Optional;

public interface WarehouseService {
    List<Warehouse> getAllWarehouses();
    Optional<Warehouse> getWarehouseById(Long id);
    Warehouse createWarehouse(Warehouse warehouse);
    Optional<Warehouse> updateWarehouse(Long id, Warehouse warehouse);
    void deleteWarehouse(Long id);
}