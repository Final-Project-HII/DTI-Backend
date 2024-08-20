package com.hii.finalProject.city.entity;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "city")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 255, nullable = false)
    private String name;
}
