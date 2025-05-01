package org.example.expert.s3.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

	private final S3Client s3Client;
	private final S3Presigner s3Presigner;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

	public String uploadFile(MultipartFile file) {
		String key = UUID.randomUUID() + "_" + file.getOriginalFilename();

		try {
			PutObjectRequest putRequest = PutObjectRequest.builder()
				.bucket(bucket)
				.key(key)
				.contentType(file.getContentType())
				.build();

			s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

			return key;
		} catch (IOException e) {
			log.error("{}", e.getMessage());
			return null;
		}
	}

	public String generateSignedUrl(String key) {
		if (key == null || key.startsWith("http")) {
			return key;
		}

		try {
			GetObjectRequest getObjectRequest = GetObjectRequest.builder()
				.bucket(bucket)
				.key(key)
				.build();

			GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
				.getObjectRequest(getObjectRequest)
				.signatureDuration(Duration.ofMinutes(10))
				.build();

			return s3Presigner.presignGetObject(presignRequest)
				.url()
				.toString();
		} catch (Exception e) {
			log.error("{}", e.getMessage());
			return null;
		}
	}

}