package com.hii.finalProject.courier.entity;

import com.hii.finalProject.city.entity.City;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;


import java.sql.Timestamp;
import java.time.Instant;

@Data
@Entity
@Table(name = "couriers",schema = "developmentfp")
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

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;


    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate(){
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }
}
