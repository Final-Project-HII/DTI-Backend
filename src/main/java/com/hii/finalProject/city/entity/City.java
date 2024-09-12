package com.hii.finalProject.city.entity;

import com.hii.finalProject.warehouse.entity.Warehouse;
import jakarta.persistence.*;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;


@Data
@Entity
@Table(name = "city",schema = "developmentfp")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @OneToMany(mappedBy = "city",cascade = CascadeType.ALL)
    private Set<Warehouse> warehouses = new LinkedHashSet<>();
}
