package com.hii.finalProject.warehouse.service.impl;

import com.hii.finalProject.address.entity.Address;
import com.hii.finalProject.address.service.AddressService;
import com.hii.finalProject.address.service.impl.AddressServiceImpl;
import com.hii.finalProject.address.specification.AddressListSpecification;
import com.hii.finalProject.city.entity.City;
import com.hii.finalProject.exceptions.DataNotFoundException;
import com.hii.finalProject.warehouse.dto.WarehouseDTO;
import com.hii.finalProject.warehouse.entity.Warehouse;
import com.hii.finalProject.warehouse.repository.WarehouseRepository;
import com.hii.finalProject.warehouse.service.WarehouseService;
import com.hii.finalProject.warehouse.specification.WarehouseListSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Point;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final AddressService addressService;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository, AddressService addressService) {
        this.warehouseRepository = warehouseRepository;
        this.addressService = addressService;
    }

    @Override
    public Page<Warehouse> getAllWarehouses(String name, String cityName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Warehouse> specification = Specification.where(WarehouseListSpecification.byWarehouseName(name).and(WarehouseListSpecification.byCity(cityName)).and(WarehouseListSpecification.notDeleted()));
        return warehouseRepository.findAll(specification,pageable);
    }

    @Override
    public Warehouse getWarehouseById(Long id) {
        Warehouse data = warehouseRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Warehouse with id " + id + " is not found"));
        return data;
    }



    @Override
    public Warehouse findNearestWarehouse(Long addressId) {
        Address address = addressService.getAddressById(addressId);
        return warehouseRepository.findNearestWarehouse(address.getLat(), address.getLon());
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
}