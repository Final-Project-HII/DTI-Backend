package com.hii.finalProject.city.controller;

import com.hii.finalProject.city.dto.CityDTO;
import com.hii.finalProject.city.entity.City;
import com.hii.finalProject.city.service.CityService;
import com.hii.finalProject.city.service.RajaOngkirServiceImpl;
import com.hii.finalProject.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cities")
public class CityController {


    private final CityService cityService;

    private final RajaOngkirServiceImpl rajaOngkirService;

    public CityController(CityService cityService, RajaOngkirServiceImpl rajaOngkirService) {
        this.cityService = cityService;
        this.rajaOngkirService = rajaOngkirService;
    }

    @PostMapping
    public ResponseEntity<CityDTO> createCity(@RequestBody CityDTO cityDTO) {
        CityDTO savedCity = cityService.saveCity(cityDTO);
        return ResponseEntity.ok(savedCity);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CityDTO> getCityById(@PathVariable Integer id) {
        Optional<CityDTO> city = cityService.getCityById(id);
        return city.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Response<List<CityDTO>>> getAllCities() {
        return Response.successfulResponse("All city has been fetched",cityService.getAllCities());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Integer id) {
        cityService.deleteCity(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fetch-raja-ongkir")
    public ResponseEntity<Response<String>> fetchRajaOngkirAPI() {
        rajaOngkirService.fetchAndSaveCities();
        return Response.successfulResponse("Raja Ongkir API has been fetched");
    }
}
