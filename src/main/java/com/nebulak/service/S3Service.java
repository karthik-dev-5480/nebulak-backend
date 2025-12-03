package com.nebulak.service;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import java.time.Duration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Service
public class S3Service {

    private final S3Client s3Client;
    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    

    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    
    @Value("${aws.s3.bucket-name2}")
    private String bucketName2;
    
    private final S3Presigner s3Presigner;

    public S3Service(S3Client s3Client,S3Presigner s3Presigner) {
        this.s3Client = s3Client;
		this.s3Presigner = s3Presigner;
        
    }
    
    

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName2)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        // Use String.format for cleaner URL construction
        return String.format("https://%s.s3.amazonaws.com/%s", bucketName2, fileName);
    }
    
    public String uploadFileSec(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        // Use String.format for cleaner URL construction
        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileName);
    }

    /**
     * Deletes a file from the S3 bucket.
     * @param fileUrl The full URL of the file to be deleted.
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            logger.info("File URL is null or empty, skipping deletion.");
            return;
        }

        try {
            // Extract the key from the URL.
            // Example URL: https://my-bucket.s3.amazonaws.com/uuid-filename.jpg
            // The key is "uuid-filename.jpg"
            String key = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            logger.info("Successfully deleted file with key: {}", key);

        } catch (S3Exception e) {
            logger.error("Error while deleting file from S3: {}", e.getMessage());
            // Depending on your use case, you might want to re-throw this as a custom exception
        } catch (Exception e) {
            logger.error("Error parsing S3 URL for deletion: {}", fileUrl, e);
        }
    }

	public String generateSignedUrl(String fullS3UrlFromDB, int eXPIRATION_SECONDS) {
		String objectKey = S3KeyExtractor.extractObjectKeyFromUrl(fullS3UrlFromDB);

		System.out.println(objectKey);
	    // 2. Define the S3 GetObject request for the resource
	    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
	            .bucket(bucketName) // Assumes bucketName is a class field
	            .key(objectKey)
	            .build();

	    long expirationSeconds=eXPIRATION_SECONDS;
		// 3. Create the presign request with the desired expiration time
	    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
	            .getObjectRequest(getObjectRequest)
	            // Set the duration for which the signed URL will be valid
	            .signatureDuration(Duration.ofSeconds(expirationSeconds)) 
	            .build();

	    // 4. Generate the pre-signed URL
	    // Assumes s3Presigner is an initialized class field (S3Presigner.create())
	    String signedUrl = s3Presigner.presignGetObject(presignRequest)
	            .url()
	            .toExternalForm();

	    return signedUrl;
	}
}