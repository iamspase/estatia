package com.iamspase.estatia.profile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository repo;

    @Transactional
    public Profile getOrCreate(OidcUser user) {
        return repo.findByAuth0Id(user.getSubject())
                .orElseGet(() -> createFrom(user));
    }

    private Profile createFrom(OidcUser user) {
        Profile p = new Profile();
        p.setAuth0Id(user.getSubject());
        p.setEmail(user.getEmail());
        p.setName(user.getFullName());
        p.setContactEmail(user.getEmail());
        try {
            return repo.saveAndFlush(p);
        } catch (DataIntegrityViolationException e) {
            return repo.findByAuth0Id(user.getSubject()).orElseThrow();
        }
    }

    @Transactional
    public Profile updateContact(Profile profile, ProfileFormDTO form) {
        profile.setDisplayName(form.getDisplayName());
        profile.setContactEmail(form.getContactEmail());
        profile.setPhone(form.getPhone());
        return repo.save(profile);
    }
}