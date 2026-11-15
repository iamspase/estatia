package com.iamspase.estatia.listing;

import com.iamspase.estatia.city.CityService;
import com.iamspase.estatia.neighbourhood.NeighbourhoodService;
import com.iamspase.estatia.profile.Profile;
import com.iamspase.estatia.profile.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

import static org.springframework.util.StringUtils.hasText;

@Controller
@RequestMapping("listings")
@RequiredArgsConstructor
public class ListingController {
    private final ListingService listingService;
    private final CityService cityService;
    private final ProfileService profileService;
    private final NeighbourhoodService neighbourhoodService;

    @GetMapping
    public String list(@ModelAttribute("filter") ListingFilter filter,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "newest") String sort,
                       Model model) {

        String safeSort = listingService.normalizeSort(sort);
        Page<Listing> result = listingService.search(filter, page, safeSort);

        UriComponentsBuilder qb = UriComponentsBuilder.newInstance();
        if (filter.getCityId() != null)         qb.queryParam("cityId", filter.getCityId());
        if (filter.getPropertyType() != null)   qb.queryParam("propertyType", filter.getPropertyType());
        if (filter.getListingType() != null)    qb.queryParam("listingType", filter.getListingType());
        if (filter.getMinPrice() != null) qb.queryParam("minPrice", filter.getMinPrice());
        if (filter.getMaxPrice() != null) qb.queryParam("maxPrice", filter.getMaxPrice());
        if (filter.getMinArea() != null) qb.queryParam("minArea", filter.getMinArea());
        if (filter.getMaxArea() != null) qb.queryParam("maxArea", filter.getMaxArea());
        if (filter.getMinFloor() != null) qb.queryParam("minFloor", filter.getMinFloor());
        if (filter.getMaxFloor() != null) qb.queryParam("maxFloor", filter.getMaxFloor());
        qb.queryParam("sort", safeSort);

        model.addAttribute("page", result);
        model.addAttribute("sort", safeSort);
        model.addAttribute("cities", cityService.getCitiesByAscendingOrder());
        model.addAttribute("propertyTypes", PropertyType.values());
        model.addAttribute("listingTypes", ListingType.values());
        model.addAttribute("pageUrl", "/listings?" + qb.build().encode().getQuery() + "&page=");
        return "listings/index";
    }

    @PostMapping
    public String createListing(
            @AuthenticationPrincipal OidcUser user,
            @Valid @ModelAttribute("listingForm") CreateListingFormDTO dto,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("cities", cityService.getCitiesByAscendingOrder());
            return "listings/new";
        }

        Profile owner = profileService.getOrCreate(user);
        listingService.createListing(dto, owner);

        return "redirect:/listings";
    }

    @GetMapping("/new")
    public String newIndex(Model model) {
        model.addAttribute("cities", cityService.getCitiesByAscendingOrder()); // List<City>
        model.addAttribute("listingForm", new CreateListingFormDTO());
        return "listings/new";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable String id,
                          @AuthenticationPrincipal OidcUser user,
                          Model model) {

        Listing listing = listingService.findById(UUID.fromString(id));

        boolean isOwner = user != null
                && listing.getOwner().getAuth0Id().equals(user.getSubject());

        model.addAttribute("listing", listing);
        model.addAttribute("isOwner", isOwner);
        return "listings/listing-details";            // templates/listing-details.html
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id,
                         @AuthenticationPrincipal OidcUser user,
                         RedirectAttributes redirectAttributes) {

        listingService.deleteListing(id, user.getSubject());

        redirectAttributes.addFlashAttribute("message", "Listing deleted.");
        return "redirect:/listings";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id,
                           @AuthenticationPrincipal OidcUser user,
                           Model model) {

        Listing listing = listingService.findForEdit(id, user.getSubject());

        model.addAttribute("listingId", id);
        model.addAttribute("listingForm", CreateListingFormDTO.from(listing));
        model.addAttribute("cities", cityService.getCitiesByAscendingOrder());
        model.addAttribute("neighbourhoods",
                neighbourhoodService.getNeighbourhoodsByCityId(listing.getCity().getId()));
        return "listings/listing-edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("listingForm") CreateListingFormDTO form,
                         BindingResult result,
                         @AuthenticationPrincipal OidcUser user,
                         Model model) {

        if (result.hasErrors()) {
            model.addAttribute("listingId", id);
            model.addAttribute("cities", cityService.getCitiesByAscendingOrder());
            model.addAttribute("neighbourhoods",
                    form.getCityId() != null
                            ? neighbourhoodService.getNeighbourhoodsByCityId(form.getCityId())
                            : List.of());
            return "listings/listing-edit";
        }

        listingService.updateListing(id, form, user.getSubject());
        return "redirect:/listings/" + id;
    }
}
