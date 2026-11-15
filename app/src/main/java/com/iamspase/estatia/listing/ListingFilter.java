package com.iamspase.estatia.listing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ListingFilter {
    private UUID cityId;
    private UUID neighbourhoodId;
    private PropertyType propertyType;    // apartment, house, office, garage
    private ListingType listingType;      // sale / rent
    private Double minPrice, maxPrice;
    private Float minArea, maxArea;
    private Integer minFloor, maxFloor;
    private Integer minRooms;
}