package com.hii.finalProject.city.service.impl;

import com.hii.finalProject.city.dto.CityDTO;
import com.hii.finalProject.city.entity.City;
import com.hii.finalProject.city.repository.CityRepository;
import com.hii.finalProject.city.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CityServiceImpl implements CityService {

    @Autowired
    private CityRepository cityRepository;

    @Override
    public CityDTO saveCity(CityDTO cityDTO) {
        City city = convertToEntity(cityDTO);
        City savedCity = cityRepository.save(city);
        return convertToDTO(savedCity);
    }

    @Override
    public Optional<CityDTO> getCityById(Integer id) {
        return cityRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    public List<CityDTO> getAllCities() {
        return cityRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCity(Integer id) {
        cityRepository.deleteById(id);
    }

    private CityDTO convertToDTO(City city) {
        CityDTO dto = new CityDTO();
        dto.setId(Math.toIntExact(city.getId()));
        dto.setName(city.getName());
        return dto;
    }

    private City convertToEntity(CityDTO dto) {
        City city = new City();
        city.setId(Long.valueOf(dto.getId()));
        city.setName(dto.getName());
        return city;
    }
}
