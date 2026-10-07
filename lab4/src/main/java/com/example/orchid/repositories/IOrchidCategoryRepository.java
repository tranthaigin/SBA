package com.example.orchid.repositories;

import com.example.orchid.pojos.OrchidCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IOrchidCategoryRepository extends JpaRepository<OrchidCategory, Long> {
    Optional<OrchidCategory> findByCategoryNameIgnoreCase(String categoryName);
}
