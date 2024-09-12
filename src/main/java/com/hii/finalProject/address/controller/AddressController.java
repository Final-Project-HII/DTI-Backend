package com.hii.finalProject.address.controller;

import com.hii.finalProject.address.dto.AddressDTO;
import com.hii.finalProject.address.entity.Address;
import com.hii.finalProject.address.service.AddressService;
import com.hii.finalProject.auth.helpers.Claims;
import com.hii.finalProject.response.Response;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<Response<Address>> createAddress(@RequestBody AddressDTO addressDTO) {
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");
        return Response.successfulResponse(HttpStatus.CREATED.value(),"Address has been successfully created", addressService.createAddress(email,addressDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<Address>> updateAddress(@PathVariable Long id, @RequestBody AddressDTO addressDTO) {
        return Response.successfulResponse("Address has been successfully updated", addressService.updateAddress(id,addressDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Object>> deleteAddress(@PathVariable Long id) {
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");
        addressService.deleteAddress(id,email);
        return Response.successfulResponse("Address has been successfully deleted");
    }

    @GetMapping("")
    public ResponseEntity<Response<Page<Address>>> getAddressByUserId(@RequestParam(value = "addressLine",required = false) String addressLine, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");
        return Response.successfulResponse("All address has been successfully fetched", addressService.getAddressByUserId(email,addressLine,page,size));
    }

    @GetMapping("/check-status")
    public ResponseEntity<Response<Boolean>> checkUserAddress() {
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");
        return Response.successfulResponse("User address status has been fetched", addressService.checkUserAddress(email));
    }


    @PutMapping("branch/{id}")
    public ResponseEntity<Response<Address>> tooglePrimaryAddress(@PathVariable Long id) {
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");
        return Response.successfulResponse("User primary address has been changed", addressService.tooglePrimaryAddress(email,id));
    }

}
