package com.cruise.parkinglotto.global.kc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;


@Slf4j
@Service
@RequiredArgsConstructor
public class ObjectStorageService {

    private final S3Client s3Client;
    private final ObjectStorageConfig objectStorageConfig;

    /**
     * @param directory: ObjectStorageConfig 및 application.yml에 정의되어있는 object storage 내 디렉토리
     * @param objectKey: 업로드할 파일의 이름
     * @param file:      업로드할 파일 객체
     * @return
     */
    public String uploadObject(String directory, String objectKey, MultipartFile file) {
        try {
            Path tempFile = Files.createTempFile("upload-", file.getOriginalFilename());
            Files.write(tempFile, file.getBytes(), StandardOpenOption.WRITE);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(objectStorageConfig.getBucket())
                    .key(directory + "/" + objectKey)
                    .build();
            s3Client.putObject(putObjectRequest, tempFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = objectStorageConfig.getS3Endpoint() + "/v1/" + objectStorageConfig.getProjectId() + "/" + objectStorageConfig.getBucket() + "/" + directory + "/" + objectKey;
        return url;
    }

    /**
     * @param fileUrl: 삭제할 파일의 url. (DB에 저장되어있는 문자열)
     */
    public void deleteObject(String fileUrl) {
        try {
            String[] urlParts = fileUrl.split("/");
            String bucketName = urlParts[5];
            String directory = urlParts[6];
            String encodedObjectKey = urlParts[7];
            log.info("bucketName: " + bucketName);
            log.info("directory: " + directory);
            log.info("encodedObjectKey: " + encodedObjectKey);

            String objectKey = URLDecoder.decode(encodedObjectKey, "UTF-8");
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(directory + "/" + objectKey)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param fileUrl: 삭제할 파일의 url. (DB에 저장되어있는 문자열)
     */
    public void deleteCertificateFileObject(String fileUrl) {
        try {
            String[] urlParts = fileUrl.split("/");
            String bucketName = urlParts[5];
            String directory = urlParts[6];
            String encodedObjectKey = urlParts[7];
            log.info("bucketName: " + bucketName);
            log.info("directory: " + directory);
            log.info("encodedObjectKey: " + encodedObjectKey);

            String objectKey = URLDecoder.decode(encodedObjectKey, "UTF-8");
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(directory + "/" + objectKey)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * @param fileUrl: 삭제할 파일의 url. (DB에 저장되어있는 문자열)
     * @return 객체가 존재하면 true, 아니면 false
     */
    public boolean doesObjectCertificateFileUrlExist(String fileUrl) {
        try {
            String[] urlParts = fileUrl.split("/");
            String bucketName = urlParts[5];
            String directory = urlParts[6];
            String objectKey = urlParts[7];

            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(directory + "/" + objectKey)
                    .build();

            HeadObjectResponse response = s3Client.headObject(headObjectRequest);
            return response != null;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return false;
            }
            e.printStackTrace();
            return false;
        }
    }
}
