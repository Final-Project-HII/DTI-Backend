
package com.hii.finalProject.address.service.impl;

import com.hii.finalProject.address.dto.AddressDTO;
import com.hii.finalProject.address.entity.Address;
import com.hii.finalProject.address.repository.AddressRepository;
import com.hii.finalProject.address.service.AddressService;
import com.hii.finalProject.address.specification.AddressListSpecification;
import com.hii.finalProject.city.entity.City;
import com.hii.finalProject.exceptions.DataNotFoundException;
import com.hii.finalProject.users.entity.User;
import com.hii.finalProject.users.service.UserService;
import com.hii.finalProject.users.service.impl.UserServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
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
    public Address createAddress(String email, AddressDTO addressDTO) {
        Long userId = userService.getUserByEmail(email);
        Address newAddress = convertToEntity(addressDTO);
        User user = new User();
        user.setId(userId);
        newAddress.setUser(user);
        List<Address> activeAddresses = addressRepository.findByUserIdAndDeletedAtIsNull(userId);
        boolean hasActiveAddress = activeAddresses.stream().anyMatch(Address::getIsActive);
        if (!hasActiveAddress) {
            newAddress.setIsActive(true);
        }
        return addressRepository.save(newAddress);
    }

    @Override
    public Address updateAddress(Long id, AddressDTO addressDTO) {
        Address existingAddress = addressRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Address with id " + id +" is not found"));
        existingAddress.setName(addressDTO.getRecipientName());
        City city = new City();
        city.setId(addressDTO.getCityId());
        existingAddress.setCity(city);
        existingAddress.setAddressLine(addressDTO.getAddressLine());
        existingAddress.setLat(addressDTO.getLat());
        existingAddress.setLon(addressDTO.getLon());
        existingAddress.setPhoneNumber(addressDTO.getPhoneNumber());
        existingAddress.setPostalCode(addressDTO.getPostalCode());
        return addressRepository.save(existingAddress);
    }

    @Override
    public void deleteAddress(Long id,String email) {
        Address address = addressRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Address with ID " + id + " is not found"));
        address.setDeletedAt(Instant.now());
        addressRepository.save(address);
        Long userId = userService.getUserByEmail(email);
        List<Address> addressList = addressRepository.findByUserIdAndDeletedAtIsNull(userId);
        if(!addressList.isEmpty()){
            Address addressData = addressList.get(0);
            addressData.setIsActive(true);
            addressRepository.save(addressData);
        }
    }

    @Override
    public Address getAddressById(Long id){
        return addressRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Address with ID " + id + " is not found" ));
    }

    @Override
    public Page<Address> getAddressByUserId(String email, String addressLine, int page, int size) {
        Long userId = userService.getUserByEmail(email);
        Pageable pageable = PageRequest.of(page, size);
        Specification<Address> specification = Specification.where(AddressListSpecification.byAddressLine(addressLine).and(AddressListSpecification.byUserId(userId)).and(AddressListSpecification.notDeleted()));
        return addressRepository.findAll(specification,pageable);
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        return null;
    }

    @Override
    public Address getActiveUserAddress(String email) {
        Long userId = userService.getUserByEmail(email);
        Address existingAddress = addressRepository.findByUserIdAndIsActiveTrueAndDeletedAtIsNull(userId).orElseThrow(() -> new DataNotFoundException("Active user address not found"));
        return existingAddress;
    }


    @Override
    public Address tooglePrimaryAddress(String email, Long addressId) {
        Long userId = userService.getUserByEmail(email);
        Optional<Address> existingAddress = addressRepository.findByUserIdAndIsActiveTrueAndDeletedAtIsNull(userId);
        existingAddress.ifPresent(address -> {
            address.setIsActive(false);
            addressRepository.save(address);
        });

        Address updatedAddress = addressRepository.findById(addressId).orElseThrow(() -> new DataNotFoundException("Address with id " + addressId + " is not found"));
        updatedAddress.setIsActive(true);
        return addressRepository.save(updatedAddress);
    }

    private Address convertToEntity(AddressDTO dto) {
        Address address = new Address();
        address.setName(dto.getRecipientName());
        City city = new City();
        city.setId(dto.getCityId());
        address.setCity(city);
        address.setPhoneNumber(dto.getPhoneNumber());
        address.setAddressLine(dto.getAddressLine());
        address.setPostalCode(dto.getPostalCode());
        address.setLat(dto.getLat());
        address.setLon(dto.getLon());
        return address;
    }
}

