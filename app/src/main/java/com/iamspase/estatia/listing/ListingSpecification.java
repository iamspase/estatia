package com.iamspase.estatia.listing;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class ListingSpecification {
    private ListingSpecification() {}

    public static Specification<Listing> from(ListingFilter f) {
        return (root, q, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (f.getCityId() != null)
                p.add(cb.equal(root.get("city").get("id"), f.getCityId()));
            if (f.getNeighbourhoodId() != null)
                p.add(cb.equal(root.get("neighbourhood").get("id"), f.getNeighbourhoodId()));
            if (f.getPropertyType() != null)
                p.add(cb.equal(root.get("propertyType"), f.getPropertyType()));
            if (f.getListingType() != null)
                p.add(cb.equal(root.get("listingType"), f.getListingType()));
            if (f.getMinPrice() != null)
                p.add(cb.greaterThanOrEqualTo(root.<Double>get("pricing"), f.getMinPrice()));
            if (f.getMaxPrice() != null)
                p.add(cb.lessThanOrEqualTo(root.<Double>get("pricing"), f.getMaxPrice()));
            if (f.getMinArea() != null)
                p.add(cb.greaterThanOrEqualTo(root.<Float>get("area"), f.getMinArea()));
            if (f.getMaxArea() != null)
                p.add(cb.lessThanOrEqualTo(root.<Float>get("area"), f.getMaxArea()));
            if (f.getMinFloor() != null)
                p.add(cb.greaterThanOrEqualTo(root.<Integer>get("floor"), f.getMinFloor()));
            if (f.getMaxFloor() != null)
                p.add(cb.lessThanOrEqualTo(root.<Integer>get("floor"), f.getMaxFloor()));
            if (f.getMinRooms() != null)
                p.add(cb.greaterThanOrEqualTo(root.<Integer>get("rooms"), f.getMinRooms()));
            return cb.and(p.toArray(new Predicate[0]));
        };
    }
}