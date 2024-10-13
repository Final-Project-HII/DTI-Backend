package com.hii.finalProject.city.service;

import com.hii.finalProject.city.dto.CityDTO;

import java.util.List;
import java.util.Optional;

public interface CityService {

    CityDTO saveCity(CityDTO cityDTO);

    Optional<CityDTO> getCityById(Integer id);

    List<CityDTO> getAllCities();

    void deleteCity(Integer id);
}
