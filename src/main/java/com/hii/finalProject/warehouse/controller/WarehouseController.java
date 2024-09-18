package com.hii.finalProject.warehouse.controller;

import com.hii.finalProject.response.Response;
import com.hii.finalProject.warehouse.dto.WarehouseDTO;
import com.hii.finalProject.warehouse.dto.WarehouseDetailResponseDto;
import com.hii.finalProject.warehouse.entity.Warehouse;
import com.hii.finalProject.warehouse.service.WarehouseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {
    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }



    @GetMapping("")
    public ResponseEntity<Response<List<Warehouse>>> getWarehouseList(){
        return Response.successfulResponse("Warehouse list is successfully fetched", warehouseService.getAllWarehouses());
    }

    @PostMapping("")
    public ResponseEntity<Response<Warehouse>> addNewWarehouse(@RequestBody WarehouseDTO data){
        return Response.successfulResponse(HttpStatus.CREATED.value(),"Warehouse has been successfully created",warehouseService.createWarehouse(data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<Warehouse>> updateWarehouse(@PathVariable("id") Long id, @RequestBody WarehouseDTO data){
        return Response.successfulResponse("Warehouse has been successfully updated",warehouseService.updateWarehouse(id,data));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Object>> deleteWarehouse(@PathVariable("id") Long id){
        warehouseService.deleteWarehouse(id);
        return Response.successfulResponse("Warehouse has been successfully deleted");
    }
    @GetMapping("/{id}")
    public ResponseEntity<Response<WarehouseDetailResponseDto>> getWarehouseById(@PathVariable Long id) {
        WarehouseDetailResponseDto warehouseDetail = warehouseService.getWarehouseDetailById(id);
        return Response.successfulResponse(
                "Warehouse detail fetched successfully",
                warehouseDetail
        );
    }
}