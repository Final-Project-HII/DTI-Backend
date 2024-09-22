package com.hii.finalProject.courier.repository;

import com.hii.finalProject.city.entity.City;
import com.hii.finalProject.courier.entity.Courier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CourierRepository extends JpaRepository<Courier, Long> {
    List<Courier> findByOriginAndDestinationAndWeight(City origin, City destination,int weight);
}
