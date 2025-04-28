package com.epam.edp.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Pavlo_Yemelianov
 */
@RestController
public class HelloEdpController {


    private final S3Client s3Client;

    @Value("${s3.bucket.name}")
    private String bucketName;

    // 🔥 Інжектуємо готовий S3Client через конструктор
    public HelloEdpController(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @GetMapping("/")
    public Map<String, String> getDataFromS3() {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key("cmtr-f9iioa28/data.txt")
                    .build();

            ResponseInputStream<GetObjectResponse> objectInputStream = s3Client.getObject(getObjectRequest);

            String content = new BufferedReader(new InputStreamReader(objectInputStream))
                    .lines()
                    .collect(Collectors.joining("\n"));

            return Map.of("content", content);
        } catch (Exception e) {
            return Map.of("error", "Failed to fetch file: " + e.getMessage());
        }
    }
}
