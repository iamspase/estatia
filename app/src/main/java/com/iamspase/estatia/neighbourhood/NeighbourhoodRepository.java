package com.iamspase.estatia.neighbourhood;

import com.iamspase.estatia.city.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NeighbourhoodRepository extends JpaRepository<Neighbourhood, UUID> {
    List<Neighbourhood> findAllByCityIdOrderByName(UUID city_id);
}
