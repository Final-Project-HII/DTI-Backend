package com.hii.finalProject.warehouse.repository;

import com.hii.finalProject.warehouse.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface WarehouseRepository extends JpaRepository<Warehouse, Long>, JpaSpecificationExecutor<Warehouse> {
        @Query(value = "SELECT w FROM Warehouse w ORDER BY ST_Distance(ST_MakePoint(:longitude,:latitude), ST_MakePoint(w.lon, w.lat)) LIMIT 1")
        Warehouse findNearestWarehouse(@Param("longitude") float longitude, @Param("latitude") float latitude);

}
