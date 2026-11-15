package com.iamspase.estatia.minio;

import io.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

@Service
public class MinioImageService {

    private static final Map<String, String> ALLOWED_TYPES = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp"
    );

    private final MinioClient client;

    @Value("${minio.bucket}")
    private String bucket;

    public MinioImageService(MinioClient client) {
        this.client = client;
    }

    public String upload(UUID listingId, MultipartFile file) throws Exception {
        String extension = ALLOWED_TYPES.get(file.getContentType());
        if (extension == null) {
            throw new IllegalArgumentException("Unsupported image type: " + file.getContentType());
        }

        String key = listingId + "/" + UUID.randomUUID() + extension;

        try (InputStream in = file.getInputStream()) {
            client.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .stream(in, file.getSize(), (long) -1)
                    .contentType(file.getContentType())
                    .build());
        } catch (Exception e) {
            throw new Exception("Failed to upload image", e);
        }
        return key;
    }

    // used by the seeder
    public String upload(UUID listingId, byte[] data, String contentType) throws Exception {
        String extension = ALLOWED_TYPES.get(contentType);
        if (extension == null) {
            throw new IllegalArgumentException("Unsupported image type: " + contentType);
        }

        String key = listingId + "/" + UUID.randomUUID() + extension;

        try (InputStream in = new ByteArrayInputStream(data)) {
            client.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .stream(in, (long) data.length, (long) -1)
                    .contentType(contentType)
                    .build());
        } catch (Exception e) {
            throw new Exception("Failed to upload image", e);
        }
        return key;
    }

    public InputStream download(String key) throws Exception {
        try {
            return client.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .build());
        } catch (Exception e) {
            throw new Exception("Failed to read image " + key, e);
        }
    }

    public String contentType(String key) throws Exception {
        try {
            return client.statObject(StatObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .build()).contentType();
        } catch (Exception e) {
            throw new Exception("Failed to stat image " + key, e);
        }
    }

    public void delete(String key) throws Exception {
        try {
            client.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .build());
        } catch (Exception e) {
            throw new Exception("Failed to delete image " + key, e);
        }
    }
}
