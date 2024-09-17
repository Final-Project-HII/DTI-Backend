package com.hii.finalProject.warehouse.specification;

import com.hii.finalProject.address.entity.Address;
import com.hii.finalProject.city.entity.City;
import com.hii.finalProject.warehouse.entity.Warehouse;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public interface WarehouseListSpecification {

    public static Specification<Warehouse> byWarehouseName(String name){
        return ((root, query, cb) -> {
            if(name == null || name.isEmpty()){
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("name")),"%" + name.toLowerCase() + "%");
        });
    }
    public static Specification<Warehouse> byCity(String city){
        return((root, query, cb) -> {
            if(city == null || city.isEmpty()){
                return cb.conjunction();
            }
            Join<Address, City> cityJoin= root.join("city", JoinType.LEFT);
            return cb.equal(cb.lower(cityJoin.get("name")), city.toLowerCase());
        });
    }

    public static Specification<Warehouse> notDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNull(root.get("deletedAt"));
    }
}
