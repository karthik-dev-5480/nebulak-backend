package com.nebulak.service;

import java.net.URL;

public class S3KeyExtractor {

    private S3KeyExtractor() {}

    public static String extractObjectKeyFromUrl(String fullS3Url) {
        if (fullS3Url == null || fullS3Url.isEmpty()) {
            throw new IllegalArgumentException("S3 URL cannot be null or empty.");
        }
        try {
            URL url = new URL(fullS3Url);
            String path = url.getPath();

            // S3 paths always start with a '/', which represents the root of the bucket.
            if (path.startsWith("/")) {
                path = path.substring(1);
                System.out.println(path);
            }
            return path;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse S3 URL: " + fullS3Url, e);
        }
    }
}