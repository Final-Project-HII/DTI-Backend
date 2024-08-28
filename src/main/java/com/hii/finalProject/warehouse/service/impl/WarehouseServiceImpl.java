package com.hii.finalProject.warehouse.service.impl;

import com.hii.finalProject.city.entity.City;
import com.hii.finalProject.exceptions.DataNotFoundException;
import com.hii.finalProject.warehouse.dto.WarehouseDTO;
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
}