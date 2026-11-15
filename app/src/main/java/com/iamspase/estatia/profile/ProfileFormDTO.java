package com.iamspase.estatia.profile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileFormDTO {

    @Size(max = 60)
    private String displayName;

    @NotBlank
    @Email
    @Size(max = 120)
    private String contactEmail;

    @Pattern(regexp = "^\\+?[0-9 ()-]{6,20}$", message = "Enter a valid phone number")
    private String phone;

    public static ProfileFormDTO from(Profile p) {
        ProfileFormDTO f = new ProfileFormDTO();
        f.setDisplayName(p.getDisplayName());
        f.setContactEmail(p.getContactEmail());
        f.setPhone(p.getPhone());
        return f;
    }
}
