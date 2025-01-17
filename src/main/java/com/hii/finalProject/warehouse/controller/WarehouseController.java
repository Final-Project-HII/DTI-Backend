package com.hii.finalProject.warehouse.controller;

import com.hii.finalProject.auth.helpers.Claims;
import com.hii.finalProject.response.Response;
import com.hii.finalProject.warehouse.dto.WarehouseDTO;
import com.hii.finalProject.warehouse.dto.WarehouseDetailResponseDto;
import com.hii.finalProject.warehouse.entity.Warehouse;
import com.hii.finalProject.warehouse.service.WarehouseService;
import org.springframework.data.domain.Page;
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
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_SUPER')")
    public ResponseEntity<Response<Page<Warehouse>>> getWarehouseList(@RequestParam(value = "name",required = false) String name, @RequestParam(value = "cityName",required = false) String cityName, @RequestParam(defaultValue = "0") int page, @RequestParam(required = false) Integer size){
        return Response.successfulResponse("Warehouse list is successfully fetched", warehouseService.getAllWarehouses(name,cityName, page,size));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_USER') or hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_SUPER')")
    public ResponseEntity<Response<Warehouse>> getWarehouseById(@PathVariable("id") Long id){
        return Response.successfulResponse("Warehouse with id " + id +" is successfully fetched", warehouseService.getWarehouseById(id));
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('SCOPE_SUPER')")
    public ResponseEntity<Response<Warehouse>> addNewWarehouse(@RequestBody WarehouseDTO data){
        return Response.successfulResponse(HttpStatus.CREATED.value(),"Warehouse has been successfully created",warehouseService.createWarehouse(data));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_SUPER')")
    public ResponseEntity<Response<Warehouse>> updateWarehouse(@PathVariable("id") Long id, @RequestBody WarehouseDTO data){
        System.out.println(data);
        return Response.successfulResponse("Warehouse has been successfully updated",warehouseService.updateWarehouse(id,data));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_SUPER')")
    public ResponseEntity<Response<Object>> deleteWarehouse(@PathVariable("id") Long id){
        warehouseService.deleteWarehouse(id);
        return Response.successfulResponse("Warehouse has been successfully deleted");
    }


    @GetMapping("/nearest-warehouse")
    @PreAuthorize("hasAuthority('SCOPE_USER') or hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_SUPER')")
    public ResponseEntity<Response<Warehouse>> getNearestWarehouse(){
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");
        return Response.successfulResponse("Nearest warehouse is successfully fetched", warehouseService.findNearestWarehouse(email));
    }
}