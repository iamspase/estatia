package com.iamspase.estatia.listing;

import lombok.Getter;

@Getter
public enum ListingType {
    SALE("For sale"),
    RENT("For rent");

    private final String label;
    ListingType(String label) { this.label = label; }
}