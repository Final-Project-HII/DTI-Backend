package com.hii.finalProject.address.controller;

import com.hii.finalProject.address.dto.AddressDTO;
import com.hii.finalProject.address.entity.Address;
import com.hii.finalProject.address.service.AddressService;
import com.hii.finalProject.auth.helpers.Claims;
import com.hii.finalProject.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public AddressDTO createAddress(@RequestBody AddressDTO addressDTO) {
        return addressService.createAddress(addressDTO);
    }

    @PutMapping("/{id}")
    public AddressDTO updateAddress(@PathVariable Long id, @RequestBody AddressDTO addressDTO) {
        return addressService.updateAddress(id, addressDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<List<Address>>> getAddressByUserId() {
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");
        return Response.successfulResponse("All address has been successfully fetched", addressService.getAddressByUserId(email));
    }

    @GetMapping
    public List<AddressDTO> getAllAddresses() {
        return addressService.getAllAddresses();
    }
}
