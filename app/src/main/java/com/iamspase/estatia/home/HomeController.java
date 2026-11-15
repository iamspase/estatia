package com.iamspase.estatia.home;

import com.iamspase.estatia.city.CityService;
import com.iamspase.estatia.listing.ListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final CityService cityService;
    private final ListingService listingService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("cities", cityService.getCitiesByAscendingOrder());
        model.addAttribute("latest", listingService.findLatest(6));
        return "index";
    }
}
