package com.iamspase.estatia.listing;

import com.iamspase.estatia.city.City;
import com.iamspase.estatia.neighbourhood.Neighbourhood;
import com.iamspase.estatia.profile.Profile;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@Table(name = "listings")
public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyType propertyType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListingType listingType;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city; // city class

    @ManyToOne
    @JoinColumn(name = "neighbourhood_id")
    private Neighbourhood neighbourhood;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id")
    private Profile owner;

    @BatchSize(size = 12)
    @ElementCollection
    @CollectionTable(name = "listing_images", joinColumns = @JoinColumn(name = "listing_id"))
    @Column(name = "object_key", nullable = false)
    @OrderColumn(name = "position")
    private List<String> images = new ArrayList<>();

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private float area;

    private int floor;

    @Column(nullable = false)
    private int rooms;

    private double pricing;

    private int listingVisits = 0;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
