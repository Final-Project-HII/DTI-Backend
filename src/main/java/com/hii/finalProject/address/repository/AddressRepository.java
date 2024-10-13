package com.hii.finalProject.address.repository;

import com.hii.finalProject.address.entity.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long>, JpaSpecificationExecutor<Address> {

    List<Address> findByUserId(Long userId);

    List<Address> findByUserIdAndDeletedAtIsNull(Long userId);

    Page<Address> findAll(Specification<Address> spec, Pageable pageable);

    Optional<Address> findByUserIdAndIsActiveTrueAndDeletedAtIsNull(Long userId);

}