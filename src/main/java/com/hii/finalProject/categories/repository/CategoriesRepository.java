package com.hii.finalProject.categories.repository;

import com.hii.finalProject.categories.entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Long>, JpaSpecificationExecutor<Categories> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Categories> findByIdAndDeletedAtIsNull(Long id);
    List<Categories> findAllByDeletedAtIsNull();
    Optional<Categories> findByNameIgnoreCase(String name);
}
