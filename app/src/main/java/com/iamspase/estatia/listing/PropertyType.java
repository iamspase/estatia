package com.iamspase.estatia.listing;

import lombok.Getter;

@Getter
public enum PropertyType {
    APARTMENT("Apartment"),
    HOUSE("House"),
    OFFICE("Office"),
    GARAGE("Garage");

    private final String label;
    PropertyType(String label) { this.label = label; }
}
