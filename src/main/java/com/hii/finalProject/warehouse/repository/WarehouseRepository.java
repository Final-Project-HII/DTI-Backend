package com.hii.finalProject.warehouse.repository;

import com.hii.finalProject.warehouse.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long>, JpaSpecificationExecutor<Warehouse> {
        @Query(value = "SELECT w.* FROM warehouse w WHERE w.deleted_at IS NULL ORDER BY ST_Distance(ST_MakePoint(:longitude,:latitude), ST_MakePoint(w.lon, w.lat)) LIMIT 1", nativeQuery = true)
        Warehouse findNearestWarehouse(@Param("longitude") float longitude, @Param("latitude") float latitude);
        @Query(value = "SELECT w.* FROM warehouse w ORDER BY ST_Distance(ST_MakePoint(:longitude,:latitude), ST_MakePoint(w.lon, w.lat))", nativeQuery = true)
        List<Warehouse> findAllOrderByDistance(@Param("longitude") float longitude, @Param("latitude") float latitude);

}
