package com.hii.finalProject.rajaongkir;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hii.finalProject.city.entity.City;
import com.hii.finalProject.city.repository.CityRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
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


    public List<ShippingCostDTO> getShippingCosts(String origin, String destination, int weight) {
        List<String> couriers = Arrays.asList("jne", "tiki", "pos");

        List<CompletableFuture<ShippingCostDTO>> futures = couriers.stream()
                .map(courier -> CompletableFuture.supplyAsync(() ->
                        getShippingCostForCourier(origin, destination, weight, courier)))
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    private ShippingCostDTO getShippingCostForCourier(String origin, String destination, int weight, String courier) {
        String url = baseUrl + "/cost";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("key", apiKey);

        String requestBody = String.format("origin=%s&destination=%s&weight=%d&courier=%s",
                origin, destination, weight, courier);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        Map<String, Object> rajaongkir = (Map<String, Object>) response.getBody().get("rajaongkir");
        List<Map<String, Object>> results = (List<Map<String, Object>>) rajaongkir.get("results");
        List<Map<String, Object>> costs = (List<Map<String, Object>>) results.get(0).get("costs");

        int cheapestCost = costs.stream()
                .flatMap(cost -> ((List<Map<String, Object>>) cost.get("cost")).stream())
                .mapToInt(cost -> (int) cost.get("value"))
                .min()
                .orElse(-1);

        return new ShippingCostDTO(courier, cheapestCost);
    }


    private City mapToCity(RajaOngkirCityResult result) {
        City city = new City();
        System.out.println(result.getCityId());
        System.out.println(result.getCityName());
        city.setId(Long.parseLong(result.getCityId()));
        city.setName(result.getCityName());
        return city;
    }
    @Data
    class RajaOngkirResponse {
        private RajaOngkirData rajaongkir;
    }

    @Data
    class RajaOngkirData {
        @JsonProperty("results")
        private List<RajaOngkirCityResult> results;
    }


    @Data
    class RajaOngkirCityResult {
        @JsonProperty("city_id")
        private String cityId;

        @JsonProperty("city_name")
        private String cityName;
    }
}


