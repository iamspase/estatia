package com.iamspase.estatia.city;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityService {
    private final CityRepository cityRepository;

    public List<City> getCitiesByAscendingOrder() {
        return cityRepository.findAllByOrderByNameAsc();
    }
}
