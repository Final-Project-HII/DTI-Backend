package com.hii.finalProject.courier.service.impl;

import com.hii.finalProject.address.entity.Address;
import com.hii.finalProject.address.service.AddressService;
import com.hii.finalProject.cart.service.CartService;
import com.hii.finalProject.cart.service.impl.CartServiceImpl;
import com.hii.finalProject.courier.dto.CourierDTO;
import com.hii.finalProject.courier.dto.CourierDataRequestDTO;
import com.hii.finalProject.courier.entity.Courier;
import com.hii.finalProject.courier.repository.CourierRepository;
import com.hii.finalProject.courier.service.CourierService;
import com.hii.finalProject.city.entity.City;
import com.hii.finalProject.city.repository.CityRepository;
import com.hii.finalProject.exceptions.DataNotFoundException;
import com.hii.finalProject.rajaongkir.RajaOngkirServiceImpl;
import com.hii.finalProject.rajaongkir.ShippingCostDTO;
import com.hii.finalProject.users.entity.User;
import com.hii.finalProject.users.service.UserService;
import com.hii.finalProject.warehouse.entity.Warehouse;
import com.hii.finalProject.warehouse.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourierServiceImpl implements CourierService {

    @Autowired
    private CourierRepository courierRepository;

    @Autowired
    private CityRepository cityRepository;

    private final CartService cartService;


    private final AddressService addressService;

    private final RajaOngkirServiceImpl rajaOngkirService;
    private final WarehouseService warehouseService;
    public CourierServiceImpl(CartService cartService, AddressService addressService, RajaOngkirServiceImpl rajaOngkirService, WarehouseService warehouseService) {
        this.cartService = cartService;
        this.addressService = addressService;
        this.rajaOngkirService = rajaOngkirService;
        this.warehouseService = warehouseService;
    }


    @Override
    @Transactional
    public List<CourierDTO> getAllCouriers(String email) {
        Address address = addressService.getActiveUserAddress(email);
        Warehouse warehouse = warehouseService.findNearestWarehouse(email);
        Integer weight = cartService.getCartTotalWeight(email);
        List<Courier> couriersList = courierRepository.findByOriginAndDestinationAndWeight(warehouse.getCity(),address.getCity(),weight);
        if(couriersList.isEmpty()){
            List<ShippingCostDTO> costs = rajaOngkirService.getShippingCosts(warehouse.getCity().getId().toString(), address.getCity().getId().toString(), weight);
            List<CourierDTO> courierList = new ArrayList<>();
            for(ShippingCostDTO data :costs){
                Courier courier = new Courier();
                courier.setCourier(data.getName());
                courier.setPrice(data.getCost());
                courier.setWeight(weight);
                City cityOrigin = new City();
                cityOrigin.setId(warehouse.getCity().getId());
                courier.setOrigin(cityOrigin);
                City cityDestination = new City();
                cityDestination.setId(address.getCity().getId());
                courier.setDestination(cityDestination);
                courierRepository.save(courier);
                CourierDTO courierData = new CourierDTO();
                courierData.setId(courier.getId());
                courierData.setName(courier.getCourier());
                courierData.setCost(courier.getPrice());
                courierList.add(courierData);
            }
            return courierList;
        }
        return couriersList.stream()
                .map(courier -> new CourierDTO(courier.getId(),courier.getCourier(), courier.getPrice()))
                .collect(Collectors.toList());
    }



    @Override
    public void deleteCourier(Long id) {
        courierRepository.deleteById(id);
    }

    @Override
    public Integer getCourierPrice(Long id) {
        Courier courier =  courierRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Courier with id " + id +" is not found"));
        return courier.getPrice();
    }

}
