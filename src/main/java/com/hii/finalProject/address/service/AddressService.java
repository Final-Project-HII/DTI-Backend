package com.hii.finalProject.address.service;

import com.hii.finalProject.address.dto.AddressDTO;
import com.hii.finalProject.address.entity.Address;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AddressService {
    Address createAddress(String email, AddressDTO addressDTO);
    Address updateAddress(Long id, AddressDTO addressDTO);
    void deleteAddress(Long id,String email);
    Page<Address> getAddressByUserId(String email, String addressLine, int page, int size);
    List<AddressDTO> getAllAddresses();

    Address getAddressById(Long id);


    Address getActiveUserAddress(String email);

    Boolean checkUserAddress(String email);


    Address tooglePrimaryAddress(String email, Long addressId);
}
