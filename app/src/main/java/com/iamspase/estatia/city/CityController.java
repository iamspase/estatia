package com.iamspase.estatia.city;

import com.iamspase.estatia.neighbourhood.NeighbourhoodDTO;
import com.iamspase.estatia.neighbourhood.NeighbourhoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
public class CityController {
    private final NeighbourhoodService neighbourhoodService;

    @GetMapping("/{cityId}/neighbourhoods")
    public List<NeighbourhoodDTO> neighbourhoods(
            @PathVariable UUID cityId) {

        return neighbourhoodService.getNeighbourhoodsByCityId(cityId);
    }
}
