package com.hii.finalProject.courier.controller;

import com.hii.finalProject.auth.helpers.Claims;
import com.hii.finalProject.courier.dto.CourierDTO;
import com.hii.finalProject.courier.dto.CourierDataRequestDTO;
import com.hii.finalProject.courier.service.CourierService;
import com.hii.finalProject.rajaongkir.ShippingCostDTO;
import com.hii.finalProject.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/couriers")
public class CourierController {

    private final CourierService courierService;

    public CourierController(CourierService courierService) {
        this.courierService = courierService;
    }


    @GetMapping("")
    public ResponseEntity<Response<List<CourierDTO>>> getCourierData() {
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");
        return Response.successfulResponse("Shipping data has been fetched", courierService.getAllCouriers(email));
    }

}
