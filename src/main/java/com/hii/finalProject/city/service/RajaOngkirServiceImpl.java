package com.hii.finalProject.city.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hii.finalProject.city.entity.City;
import com.hii.finalProject.city.repository.CityRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RajaOngkirServiceImpl {

    private final RestTemplate restTemplate;
    private final CityRepository cityRepository;

    @Value("${rajaongkir.api.key}")
    private String apiKey;

    @Value("${rajaongkir.api.base-url}")
    private String baseUrl;

    public RajaOngkirServiceImpl(RestTemplate restTemplate, CityRepository cityRepository) {
        this.restTemplate = restTemplate;
        this.cityRepository = cityRepository;
    }

    @Transactional
    public void fetchAndSaveCities() {
        String url = baseUrl + "/city";

        HttpHeaders headers = new HttpHeaders();
        headers.set("key", apiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<RajaOngkirResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, RajaOngkirResponse.class);

        if (response.getBody() != null && response.getBody().getRajaongkir() != null) {
            List<RajaOngkirCityResult> results = response.getBody().getRajaongkir().getResults();

            if (results != null && !results.isEmpty()) {
                List<City> cities = results.stream()
                        .map(this::mapToCity)
                        .collect(Collectors.toList());
                cityRepository.saveAll(cities);
            }
        }
    }

    private City mapToCity(RajaOngkirCityResult result) {
        City city = new City();
        System.out.println(result.getCityId());
        System.out.println(result.getCityName());
        city.setId(Long.parseLong(result.getCityId()));
        city.setName(result.getCityName());
        return city;
    }
}

@Data
class RajaOngkirResponse {
    private RajaOngkirData rajaongkir;
}

@Data
class RajaOngkirData {
    @JsonProperty("results")
    private List<RajaOngkirCityResult> results;  // Changed to List
}

@Data
class RajaOngkirCityResult {
    @JsonProperty("city_id")
    private String cityId;

    @JsonProperty("city_name")
    private String cityName;

}