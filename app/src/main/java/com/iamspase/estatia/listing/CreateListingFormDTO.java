package com.iamspase.estatia.listing;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateListingFormDTO {

    @NotBlank(message = "Title is a required field.")
    @Size(min = 4, max = 64, message = "Title must be between 4 and 64 characters.")
    private String title;

    @NotBlank(message = "Description is a required field.")
    @Size(min = 20, max = 2000, message = "Description must be between 20 and 2000 characters.")
    private String description;

    @NotNull(message = "Please select a property type.")
    private PropertyType propertyType;

    @NotNull(message = "Please select a listing type.")
    private ListingType listingType;

    @NotNull(message = "Please select a city.")
    private UUID cityId;

    @NotNull(message = "Please select a neighbourhood.")
    private UUID neighbourhoodId;

    @NotBlank(message = "Street address is a required field.")
    @Size(max = 120, message = "Address cannot exceed 120 characters.")
    private String address;

    @Positive(message = "Area must be greater than zero.")
    private float area;

    private int floor;

    @Min(value = 1, message = "Property must have at least 1 room.")
    private int rooms;

    @Positive(message = "Price must be greater than zero.")
    private double pricing;

    private List<MultipartFile> files = new ArrayList<>();


    public static CreateListingFormDTO from(Listing l) {
        CreateListingFormDTO f = new CreateListingFormDTO();
        f.setPropertyType(l.getPropertyType());
        f.setListingType(l.getListingType());
        f.setTitle(l.getTitle());
        f.setDescription(l.getDescription());
        f.setCityId(l.getCity().getId());
        f.setNeighbourhoodId(l.getNeighbourhood() != null ? l.getNeighbourhood().getId() : null);
        f.setAddress(l.getAddress());
        f.setPricing(l.getPricing());
        f.setArea(l.getArea());
        f.setRooms(l.getRooms());
        return f;
    }
}