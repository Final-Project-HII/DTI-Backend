package com.hii.finalProject.warehouse.dto;

import com.hii.finalProject.city.entity.City;
import com.hii.finalProject.warehouse.entity.Warehouse;
import lombok.Data;

@Data
public class WarehouseDTO {
    private String name;
    private String addressLine;
    private Long cityId;
    private String postalCode;
    private Float lat;
    private Float lon;


    public Warehouse toEntity(){
        Warehouse newWarehouse = new Warehouse();
        newWarehouse.setName(name);
        newWarehouse.setAddressLine(addressLine);
        City city = new City();
        city.setId(cityId);
        newWarehouse.setCity(city);
        newWarehouse.setPostalCode(postalCode);
        newWarehouse.setLat(lat);
        newWarehouse.setLon(lon);
        return newWarehouse;
    }
}