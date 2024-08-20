package com.hii.finalProject.courier.entity;

import com.hii.finalProject.city.entity.City;
import jakarta.persistence.*;
import lombok.Data;


import java.sql.Timestamp;

@Data
@Entity
@Table(name = "couriers")
public class Courier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "origin")
    private City origin;

    @ManyToOne
    @JoinColumn(name = "destination")
    private City destination;

    @Column(name = "courier", nullable = false)
    private String courier;

    @Column(name = "weight")
    private Integer weight;

    @Column(name = "price")
    private Integer price;

    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
