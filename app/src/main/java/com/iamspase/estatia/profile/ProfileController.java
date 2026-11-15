package com.iamspase.estatia.profile;

import com.iamspase.estatia.listing.ListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    private final ListingService listingService;

    @GetMapping
    public String view(@AuthenticationPrincipal OidcUser user, Model model) {
        Profile profile = profileService.getOrCreate(user);
        model.addAttribute("profile", profile);
        model.addAttribute("profileForm", ProfileFormDTO.from(profile));
        model.addAttribute("myListings", listingService.findByOwner(profile));
        return "profile/index";
    }

    @PostMapping
    public String update(@AuthenticationPrincipal OidcUser user,
                         @Valid @ModelAttribute("profileForm") ProfileFormDTO form,
                         BindingResult result,
                         RedirectAttributes redirect,
                         Model model) {

        Profile profile = profileService.getOrCreate(user);

        if (result.hasErrors()) {
            model.addAttribute("profile", profile);
            model.addAttribute("myListings", listingService.findByOwner(profile));
            return "profile/index";
        }

        profileService.updateContact(profile, form);
        redirect.addFlashAttribute("saved", true);
        return "redirect:/profile";
    }
}
