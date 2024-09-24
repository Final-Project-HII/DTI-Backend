package com.hii.finalProject.courier.service.impl;

import com.hii.finalProject.address.entity.Address;
import com.hii.finalProject.address.service.AddressService;
import com.hii.finalProject.courier.dto.CourierDTO;
import com.hii.finalProject.courier.dto.CourierDataRequestDTO;
import com.hii.finalProject.courier.entity.Courier;
import com.hii.finalProject.courier.repository.CourierRepository;
import com.hii.finalProject.courier.service.CourierService;
import com.hii.finalProject.city.entity.City;
import com.hii.finalProject.city.repository.CityRepository;
import com.hii.finalProject.rajaongkir.RajaOngkirServiceImpl;
import com.hii.finalProject.rajaongkir.ShippingCostDTO;
import com.hii.finalProject.users.entity.User;
import com.hii.finalProject.users.service.UserService;
import com.hii.finalProject.warehouse.entity.Warehouse;
import com.hii.finalProject.warehouse.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourierServiceImpl implements CourierService {

    @Autowired
    private CourierRepository courierRepository;

    @Autowired
    private CityRepository cityRepository;


    private final AddressService addressService;

    private final RajaOngkirServiceImpl rajaOngkirService;
    private final WarehouseService warehouseService;
    public CourierServiceImpl(AddressService addressService, RajaOngkirServiceImpl rajaOngkirService, WarehouseService warehouseService) {
        this.addressService = addressService;
        this.rajaOngkirService = rajaOngkirService;
        this.warehouseService = warehouseService;
    }

    @Override
    public CourierDTO saveCourier(CourierDTO courierDTO) {
        Courier courier = convertToEntity(courierDTO);
        Courier savedCourier = courierRepository.save(courier);
        return convertToDTO(savedCourier);
    }

    @Override
    public Optional<CourierDTO> getCourierById(Long id) {
        return courierRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    public List<ShippingCostDTO> getAllCouriers(CourierDataRequestDTO courierDataRequestDTO, String email) {
        Address address = addressService.getActiveUserAddress(email);
        Warehouse warehouse = warehouseService.findNearestWarehouse(email);
        List<Courier> couriersList = courierRepository.findByOriginAndDestinationAndWeight(warehouse.getCity(),address.getCity(),courierDataRequestDTO.getWeight());
        if(couriersList.isEmpty()){
            List<ShippingCostDTO> costs = rajaOngkirService.getShippingCosts(warehouse.getCity().getId().toString(), address.getCity().getId().toString(), courierDataRequestDTO.getWeight());
            for(ShippingCostDTO data :costs){
                Courier courier = new Courier();
                courier.setCourier(data.getName());
                courier.setPrice(data.getCost());
                courier.setWeight(courierDataRequestDTO.getWeight());
                City cityOrigin = new City();
                cityOrigin.setId(warehouse.getCity().getId());
                courier.setOrigin(cityOrigin);
                City cityDestination = new City();
                cityDestination.setId(address.getCity().getId());
                courier.setDestination(cityDestination);
                courierRepository.save(courier);
            }
            System.out.println("Fetch from rajaOngkir API");
            return costs;
        }
        System.out.println("Fetch from database");
        return couriersList.stream()
                .map(courier -> new ShippingCostDTO(courier.getCourier(), courier.getPrice()))
                .collect(Collectors.toList());
    }



    @Override
    public void deleteCourier(Long id) {
        courierRepository.deleteById(id);
    }

    private CourierDTO convertToDTO(Courier courier) {
        CourierDTO dto = new CourierDTO();
        dto.setId(courier.getId());
        dto.setOriginCityId(Math.toIntExact(courier.getOrigin() != null ? courier.getOrigin().getId() : null));
        dto.setDestinationCityId(Math.toIntExact(courier.getDestination() != null ? courier.getDestination().getId() : null));
        dto.setCourier(courier.getCourier());
        dto.setWeight(courier.getWeight());
        dto.setPrice(courier.getPrice());
        return dto;
    }

    private Courier convertToEntity(CourierDTO dto) {
        Courier courier = new Courier();
        courier.setId(dto.getId());
        courier.setCourier(dto.getCourier());
        courier.setWeight(dto.getWeight());
        courier.setPrice(dto.getPrice());

        if (dto.getOriginCityId() != null) {
            City origin = cityRepository.findById(dto.getOriginCityId())
                    .orElseThrow(() -> new RuntimeException("City not found"));
            courier.setOrigin(origin);
        }

        if (dto.getDestinationCityId() != null) {
            City destination = cityRepository.findById(dto.getDestinationCityId())
                    .orElseThrow(() -> new RuntimeException("City not found"));
            courier.setDestination(destination);
        }

        return courier;
    }
}
