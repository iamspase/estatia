package com.iamspase.estatia.listing;

import com.iamspase.estatia.profile.Profile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ListingRepository extends JpaRepository<Listing, UUID>, JpaSpecificationExecutor<Listing> {
    List<Listing> findByOwnerOrderByCreatedAtDesc(Profile owner);

    @EntityGraph(attributePaths = {"owner", "city", "neighbourhood", "images"})
    Optional<Listing> findWithOwnerById(UUID id);

}
