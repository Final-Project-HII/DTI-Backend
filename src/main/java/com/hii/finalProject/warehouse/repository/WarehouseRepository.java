package com.hii.finalProject.warehouse.repository;

import com.hii.finalProject.warehouse.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long>, JpaSpecificationExecutor<Warehouse> {
        @Query(value = "SELECT w.* FROM developmentfp.warehouse w ORDER BY developmentfp.ST_Distance(developmentfp.ST_MakePoint(:longitude,:latitude), developmentfp.ST_MakePoint(w.lon, w.lat)) LIMIT 1", nativeQuery = true)
        Warehouse findNearestWarehouse(@Param("longitude") float longitude, @Param("latitude") float latitude);

}
