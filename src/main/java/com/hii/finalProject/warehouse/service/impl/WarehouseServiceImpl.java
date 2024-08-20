package com.hii.finalProject.warehouse.service.impl;

import com.hii.finalProject.warehouse.entity.Warehouse;
import com.hii.finalProject.warehouse.repository.WarehouseRepository;
import com.hii.finalProject.warehouse.service.WarehouseService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
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
    public Warehouse createWarehouse(Warehouse warehouse) {
        return warehouseRepository.save(warehouse);
    }

    @Override
    public Optional<Warehouse> updateWarehouse(Long id, Warehouse warehouseDetails) {
        return warehouseRepository.findById(id)
                .map(existingWarehouse -> {
                    existingWarehouse.setName(warehouseDetails.getName());
                    existingWarehouse.setAddressLine(warehouseDetails.getAddressLine());
                    existingWarehouse.setCityId(warehouseDetails.getCityId());
                    existingWarehouse.setPostalCode(warehouseDetails.getPostalCode());
                    existingWarehouse.setLat(warehouseDetails.getLat());
                    existingWarehouse.setLon(warehouseDetails.getLon());
                    return warehouseRepository.save(existingWarehouse);
                });
    }

    @Override
    public void deleteWarehouse(Long id) {
        warehouseRepository.deleteById(id);
    }
}