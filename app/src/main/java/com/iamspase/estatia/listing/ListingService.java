package com.iamspase.estatia.listing;

import com.iamspase.estatia.city.City;
import com.iamspase.estatia.city.CityRepository;
import com.iamspase.estatia.minio.MinioImageService;
import com.iamspase.estatia.neighbourhood.Neighbourhood;
import com.iamspase.estatia.neighbourhood.NeighbourhoodRepository;
import com.iamspase.estatia.profile.Profile;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListingService {
    private final ListingRepository listingRepository;
    private final CityRepository cityRepository;
    private final NeighbourhoodRepository neighbourhoodRepository;
    private final MinioImageService minioImageService;

    private static final int PAGE_SIZE = 12;
    private static final Map<String, Sort> SORTS = Map.of(
            "newest",     Sort.by(Sort.Direction.DESC, "createdAt"),
            "price_asc",  Sort.by(Sort.Direction.ASC,  "pricing"),
            "price_desc", Sort.by(Sort.Direction.DESC, "pricing"));


    @Transactional()
    public List<Listing> findByOwner(Profile owner) {
        return listingRepository.findByOwnerOrderByCreatedAtDesc(owner);
    }

    @SneakyThrows
    @Transactional
    public void createListing(CreateListingFormDTO dto, Profile profile) {
        log.info("files received: {}", dto.getFiles().size());
        dto.getFiles().forEach(f -> log.info("  {} | {} bytes | {}", f.getOriginalFilename(), f.getSize(), f.getContentType()));
        Listing listing = new Listing();
        listing.setTitle(dto.getTitle());
        listing.setDescription(dto.getDescription());
        listing.setAddress(dto.getAddress());
        listing.setArea(dto.getArea());
        listing.setFloor(dto.getFloor());
        listing.setRooms(dto.getRooms());
        listing.setPricing(dto.getPricing());
        listing.setPropertyType(dto.getPropertyType());
        listing.setListingType(dto.getListingType());
        listing.setOwner(profile);

        City city = cityRepository.findById(dto.getCityId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid city ID"));
        listing.setCity(city);

        Neighbourhood neighbourhood = neighbourhoodRepository.findById(dto.getNeighbourhoodId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid neighbourhood ID"));
        listing.setNeighbourhood(neighbourhood);

        Listing saved = listingRepository.save(listing);

        List<String> uploadedKeys = new ArrayList<>();
        try {
            for (MultipartFile file : dto.getFiles()) {
                if (file.isEmpty()) continue;   // browser sends one empty part when nothing is chosen
                String key = minioImageService.upload(saved.getId(), file);
                uploadedKeys.add(key);
                saved.getImages().add(key);
            }
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @Transactional
    public Page<Listing> search(ListingFilter filter, int page, String sort) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0), PAGE_SIZE, SORTS.getOrDefault(sort, SORTS.get("newest")));
        Page<Listing> result = listingRepository.findAll(ListingSpecification.from(filter), pageable);
        result.getContent().forEach(l -> l.getImages().size());
        return result;
    }

    public String normalizeSort(String sort) {
        return SORTS.containsKey(sort) ? sort : "newest";
    }

    @Transactional()
    public Listing findById(UUID id) {
        return listingRepository.findWithOwnerById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public void deleteListing(UUID id, String auth0Id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!listing.getOwner().getAuth0Id().equals(auth0Id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        listingRepository.delete(listing);
    }

    @Transactional()
    public Listing findForEdit(UUID id, String auth0Id) {
        Listing listing = listingRepository.findWithOwnerById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        requireOwner(listing, auth0Id);
        return listing;
    }

    @Transactional
    public void updateListing(UUID id, CreateListingFormDTO form, String auth0Id) {
        Listing listing = listingRepository.findWithOwnerById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        requireOwner(listing, auth0Id);

        Neighbourhood neighbourhood = neighbourhoodRepository.findById(form.getNeighbourhoodId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
        if (!neighbourhood.getCity().getId().equals(form.getCityId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Neighbourhood not in city");
        }

        listing.setPropertyType(form.getPropertyType());
        listing.setListingType(form.getListingType());
        listing.setTitle(form.getTitle());
        listing.setDescription(form.getDescription());
        listing.setCity(cityRepository.getReferenceById(form.getCityId()));
        listing.setNeighbourhood(neighbourhood);
        listing.setAddress(form.getAddress());
        listing.setPricing(form.getPricing());
        listing.setArea(form.getArea());
        listing.setRooms(form.getRooms());
    }

    private void requireOwner(Listing listing, String auth0Id) {
        if (!listing.getOwner().getAuth0Id().equals(auth0Id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @Transactional
    public List<Listing> findLatest(int count) {
        List<Listing> latest = listingRepository
                .findAll(PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "createdAt")))
                .getContent();
        latest.forEach(l -> l.getImages().size());
        return latest;
    }
}
