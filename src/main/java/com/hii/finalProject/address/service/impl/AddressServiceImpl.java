
package com.hii.finalProject.address.service.impl;

import com.hii.finalProject.address.dto.AddressDTO;
import com.hii.finalProject.address.entity.Address;
import com.hii.finalProject.address.repository.AddressRepository;
import com.hii.finalProject.address.service.AddressService;
import com.hii.finalProject.users.entity.User;
import com.hii.finalProject.users.service.UserService;
import com.hii.finalProject.users.service.impl.UserServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserService userService;

    public AddressServiceImpl(AddressRepository addressRepository, UserService userService) {
        this.addressRepository = addressRepository;
        this.userService = userService;
    }

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO) {
        Address address = convertToEntity(addressDTO);
        Address savedAddress = addressRepository.save(address);
        return convertToDTO(savedAddress);
    }

    @Override
    public AddressDTO updateAddress(Long id, AddressDTO addressDTO) {
        Address address = convertToEntity(addressDTO);
        address.setId(id);
        Address updatedAddress = addressRepository.save(address);
        return convertToDTO(updatedAddress);
    }

    @Override
    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }

    @Override
    public List<Address> getAddressByUserId(String email) {
        User userData = userService.getUserByEmail(email);
        return addressRepository.findByUserId(userData.getId());
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        return addressRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private AddressDTO convertToDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setUserId(address.getUser().getId());
        dto.setAddressLine(address.getAddressLine());
        dto.setCityId(address.getCity().getId());
        dto.setPostalCode(address.getPostalCode());
        dto.setLat(address.getLat());
        dto.setLon(address.getLon());
        dto.setIsActive(address.getIsActive());
        dto.setCreatedAt(address.getCreatedAt());
        dto.setUpdatedAt(address.getUpdatedAt());
        return dto;
    }

    private Address convertToEntity(AddressDTO dto) {
        Address address = new Address();
        address.setAddressLine(dto.getAddressLine());
        address.setPostalCode(dto.getPostalCode());
        address.setLat(dto.getLat());
        address.setLon(dto.getLon());
        address.setIsActive(dto.getIsActive());
        return address;
    }
}

