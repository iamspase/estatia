package com.iamspase.estatia.neighbourhood;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NeighbourhoodService {
    private final NeighbourhoodRepository neighbourhoodRepository;

    public List<NeighbourhoodDTO> getNeighbourhoodsByCityId(UUID cityId) {
        return neighbourhoodRepository
                .findAllByCityIdOrderByName(cityId)
                .stream()
                .map(n -> new NeighbourhoodDTO(n.getId(), n.getName()))
                .toList();
    }
}
