package com.cruise.parkinglotto.global.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager {

    private final AmazonS3 amazonS3;
    private final AmazonConfig amazonConfig;

    public String uploadFileToDirectory(String directoryPath, String keyName, MultipartFile file) {
        String fullKeyName = directoryPath + "/" + keyName;
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        try {
            amazonS3.putObject(new PutObjectRequest(amazonConfig.getBucket(), fullKeyName, file.getInputStream(), metadata));
        } catch (IOException e) {
            log.error("Error at AmazonS3Manager uploadFileToDirectory: {}", (Object) e.getStackTrace());
        }
        return amazonS3.getUrl(amazonConfig.getBucket(), fullKeyName).toString();
    }


    public void deleteFile(String fileUrl) throws IOException {
        // 예상된 S3 버킷 URL 패턴
        String s3UrlPattern = amazonConfig.getS3Url();

        // URL이 예상된 패턴으로 시작하는지 확인
        if (!fileUrl.startsWith(s3UrlPattern)) {
            throw new IllegalArgumentException("Invalid S3 URL: " + fileUrl);
        }

        // 파일 키 추출
        String fileKey = fileUrl.substring(s3UrlPattern.length());

        log.info("s3 객체 키: " + fileKey);
        try {
            amazonS3.deleteObject(amazonConfig.getBucket(), fileKey);

        } catch (SdkClientException e) {
            log.error("Error deleting file from S3", e);
            throw new IOException("Error deleting file from S3", e);
        }
    }

    public void deleteFileFromUrl(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            String bucketName = amazonConfig.getBucket();
            String encodedKeyName = url.getPath().substring(1);
            String keyName = URLDecoder.decode(encodedKeyName, "UTF-8");
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, keyName));
            log.info("File deleted successfully: {}", keyName);
        } catch (MalformedURLException e) {
            log.error("MalformedURLException: Invalid URL provided. {}", e.getMessage());
        } catch (UnsupportedEncodingException e) {
            log.error("UnsupportedEncodingException: Failed to decode URL. {}", e.getMessage());
        } catch (AmazonServiceException e) {
            log.error("AmazonServiceException: Failed to delete file from S3. {}", e.getErrorMessage());
        } catch (SdkClientException e) {
            log.error("SdkClientException: Failed to delete file from S3. {}", e.getMessage());
        }
    }

}
