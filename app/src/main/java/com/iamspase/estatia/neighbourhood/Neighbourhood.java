package com.iamspase.estatia.neighbourhood;

import com.iamspase.estatia.city.City;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "neighbourhoods", uniqueConstraints = @UniqueConstraint(name = "uq_neighbourhoods_city_name",
        columnNames = {"city_id", "name"}))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Neighbourhood {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Column(name = "created_at")
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Timestamp updatedAt;
}
