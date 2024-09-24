package com.hii.finalProject.courier.service;

import com.hii.finalProject.courier.dto.CourierDTO;
import com.hii.finalProject.courier.dto.CourierDataRequestDTO;
import com.hii.finalProject.rajaongkir.ShippingCostDTO;

import java.util.List;
import java.util.Optional;

public interface CourierService {


    List<CourierDTO> getAllCouriers(String email);

    void deleteCourier(Long id);

    Integer getCourierPrice(Long id);
}
