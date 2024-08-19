package com.hii.finalProject.address.service;

import com.hii.finalProject.address.dto.AddressDTO;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO);
    AddressDTO updateAddress(Long id, AddressDTO addressDTO);
    void deleteAddress(Long id);
    AddressDTO getAddressById(Long id);
    List<AddressDTO> getAllAddresses();
}
