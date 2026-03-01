package com.galaxy13.server.service;

import com.galaxy13.server.config.MinioConfigurationProperties;
import io.minio.*;
import io.minio.http.Method;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final MinioClient minioClient;

    private final MinioConfigurationProperties properties;

    public String uploadFile(MultipartFile file, UUID userId, String gameSlug) throws Exception {
        String fileKey = generateFileKey(userId, gameSlug, file.getOriginalFilename());

        minioClient.putObject(
                PutObjectArgs.builder().bucket(properties.getBucket()).object(fileKey).stream(
                                file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build());

        log.info("File {} uploaded successfully to bucket {}", fileKey, properties.getBucket());
        return fileKey;
    }

    public String uploadFile(
            InputStream inputStream,
            long size,
            String contentType,
            UUID userId,
            String gameSlug,
            String filename)
            throws Exception {
        String fileKey = generateFileKey(userId, gameSlug, filename);

        minioClient.putObject(
                PutObjectArgs.builder().bucket(properties.getBucket()).object(fileKey).stream(
                                inputStream, size, -1)
                        .contentType(contentType)
                        .build());
        log.info("File {} uploaded successfully to bucket {}", fileKey, properties.getBucket());
        return fileKey;
    }

    public InputStream downloadFile(String fileKey) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder().bucket(properties.getBucket()).object(fileKey).build());
    }

    public void deleteFile(String fileKey) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder().bucket(properties.getBucket()).object(fileKey).build());
        log.info("Delete file {} successfully from bucket {}", fileKey, properties.getBucket());
    }

    public String getPresignedDownloadUrl(String fileKey, int expiryMinutes) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(properties.getBucket())
                        .object(fileKey)
                        .expiry(expiryMinutes, TimeUnit.MINUTES)
                        .build());
    }

    public String getPresignedUploadUrl(String fileKey, int expiryMinutes) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.PUT)
                        .bucket(properties.getBucket())
                        .object(fileKey)
                        .expiry(expiryMinutes, TimeUnit.MINUTES)
                        .build());
    }

    public StatObjectResponse getFileInfo(String fileKey) throws Exception {
        return minioClient.statObject(
                StatObjectArgs.builder().bucket(properties.getBucket()).object(fileKey).build());
    }

    public boolean fileExists(String fileKey) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(fileKey)
                            .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String calculateChecksum(InputStream inputStream) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] buffer = new byte[8192];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            messageDigest.update(buffer, 0, bytesRead);
        }

        byte[] hashBytes = messageDigest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public String calculateChecksum(MultipartFile file) throws Exception {
        try (InputStream inputStream = file.getInputStream()) {
            return calculateChecksum(inputStream);
        }
    }

    private String generateFileKey(UUID userId, String gameSlug, String fileName) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        return String.format(
                "%s/%s/%s_%s_%s", userId, gameSlug, timestamp, uniqueId, formatFileName(fileName));
    }

    private String formatFileName(String fileName) {
        if (fileName == null) {
            return "save";
        }
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
