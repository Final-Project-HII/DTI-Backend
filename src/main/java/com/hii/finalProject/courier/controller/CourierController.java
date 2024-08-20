package com.hii.finalProject.courier.controller;

import com.hii.finalProject.courier.dto.CourierDTO;
import com.hii.finalProject.courier.service.CourierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/couriers")
public class CourierController {

    @Autowired
    private CourierService courierService;

    @PostMapping
    public ResponseEntity<CourierDTO> createCourier(@RequestBody CourierDTO courierDTO) {
        CourierDTO savedCourier = courierService.saveCourier(courierDTO);
        return ResponseEntity.ok(savedCourier);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourierDTO> getCourierById(@PathVariable Long id) {
        Optional<CourierDTO> courier = courierService.getCourierById(id);
        return courier.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CourierDTO>> getAllCouriers() {
        List<CourierDTO> couriers = courierService.getAllCouriers();
        return ResponseEntity.ok(couriers);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourier(@PathVariable Long id) {
        courierService.deleteCourier(id);
        return ResponseEntity.noContent().build();
    }
}
