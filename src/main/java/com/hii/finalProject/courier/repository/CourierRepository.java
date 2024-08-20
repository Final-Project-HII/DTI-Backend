package com.hii.finalProject.courier.repository;

import com.hii.finalProject.courier.entity.Courier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourierRepository extends JpaRepository<Courier, Long> {
}
