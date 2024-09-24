package com.hii.finalProject.courier.service;

import com.hii.finalProject.courier.dto.CourierDTO;
import com.hii.finalProject.courier.dto.CourierDataRequestDTO;
import com.hii.finalProject.rajaongkir.ShippingCostDTO;

import java.util.List;
import java.util.Optional;

public interface CourierService {

    CourierDTO saveCourier(CourierDTO courierDTO);

    Optional<CourierDTO> getCourierById(Long id);

    List<ShippingCostDTO> getAllCouriers(CourierDataRequestDTO courierDataRequestDTO, String email);

    void deleteCourier(Long id);
}
