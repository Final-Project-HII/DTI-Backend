package com.hii.finalProject.address.entity;

import com.hii.finalProject.city.entity.City;
import com.hii.finalProject.users.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "address",schema = "developmentfp")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "address_line", length = 255)
    private String addressLine;

    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Column(name = "lat")
    private Float lat;

    @Column(name = "lon")
    private Float lon;

    @Column(name = "is_active")
    private Boolean isActive = false;


    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;


    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @PrePersist
    protected void onCreate(){
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}

