package com.hii.finalProject.address.service;

import com.hii.finalProject.address.dto.AddressDTO;
import com.hii.finalProject.address.entity.Address;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO);
    AddressDTO updateAddress(Long id, AddressDTO addressDTO);
    void deleteAddress(Long id);
    List<Address> getAddressByUserId(String email);
    List<AddressDTO> getAllAddresses();
}
