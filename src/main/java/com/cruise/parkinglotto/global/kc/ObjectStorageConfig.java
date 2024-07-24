package com.cruise.parkinglotto.global.kc;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Getter
@Configuration
public class ObjectStorageConfig {

    @Value("${cloud.kc.s3.url}")
    private String s3Endpoint;

    @Value("${cloud.kc.s3.project-id}")
    private String projectId;

    @Value("${cloud.kc.s3.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.kc.s3.credentials.secretKey}")
    private String secretAccessKey;

    @Value("${cloud.kc.s3.region.static}")
    private String region = "kr-central-2";

    @Value("${cloud.kc.s3.bucket}")
    private String bucket;

    @Value("${cloud.kc.s3.path.parking-space-image}")
    private String parkingSpaceImagePath;

    @Value("${cloud.kc.s3.path.map-image}")
    private String mapImagePath;

    @Bean
    public S3Client s3client() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretAccessKey)))
                .endpointOverride(URI.create(s3Endpoint))
                .forcePathStyle(true)
                .build();
    }
}
