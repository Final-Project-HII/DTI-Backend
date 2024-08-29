package com.hii.finalProject.courier.service;

import com.hii.finalProject.courier.dto.CourierDTO;

import java.util.List;
import java.util.Optional;

public interface CourierService {

    CourierDTO saveCourier(CourierDTO courierDTO);

    Optional<CourierDTO> getCourierById(Long id);

    List<CourierDTO> getAllCouriers();

    void deleteCourier(Long id);
}
