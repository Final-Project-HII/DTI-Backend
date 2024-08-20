package com.hii.finalProject.courier.service.impl;

import com.hii.finalProject.courier.dto.CourierDTO;
import com.hii.finalProject.courier.entity.Courier;
import com.hii.finalProject.courier.repository.CourierRepository;
import com.hii.finalProject.courier.service.CourierService;
import com.hii.finalProject.city.entity.City;
import com.hii.finalProject.city.repository.CityRepository;
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
    public List<CourierDTO> getAllCouriers() {
        return courierRepository.findAll().stream()
                .map(this::convertToDTO)
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
        dto.setCreatedAt(courier.getCreatedAt());
        dto.setUpdatedAt(courier.getUpdatedAt());
        return dto;
    }

    private Courier convertToEntity(CourierDTO dto) {
        Courier courier = new Courier();
        courier.setId(dto.getId());
        courier.setCourier(dto.getCourier());
        courier.setWeight(dto.getWeight());
        courier.setPrice(dto.getPrice());
        courier.setCreatedAt(dto.getCreatedAt());
        courier.setUpdatedAt(dto.getUpdatedAt());

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
