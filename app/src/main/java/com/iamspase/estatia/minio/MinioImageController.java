package com.iamspase.estatia.minio;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.Duration;
import java.util.UUID;
import java.util.regex.Pattern;

@Controller
@RequiredArgsConstructor
public class MinioImageController {

    private static final Pattern FILENAME = Pattern.compile("[0-9a-f-]{36}\\.(jpg|png|webp)");
    private final MinioImageService minioImageService;

    @GetMapping("/images/{listingId}/{filename}")
    public ResponseEntity<InputStreamResource> get(@PathVariable UUID listingId,
                                                   @PathVariable String filename) throws Exception {
        if (!FILENAME.matcher(filename).matches()) {
            return ResponseEntity.notFound().build();
        }
        String key = listingId + "/" + filename;

        String contentType;
        try {
            contentType = minioImageService.contentType(key);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .cacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic().immutable())
                .body(new InputStreamResource(minioImageService.download(key)));
    }
}