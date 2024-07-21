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

    /**
     *
     * @param directoryPath: S3 버킷에 만들어둔 디렉토리명을 의미합니다. application.yml에 환경변수로 설정 후, AmazonConfig에 선언해주시면 됩니다.
     * @param keyName: 저장할 객체의 이름을 의미합니다. 저장하고 싶은 문자열로 넣어주시면 됩니다.
     * @param file: 업로드할 피일 객체
     * @return
     */
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

    /**
     *
     * @param fileUrl: 데이테베이스에 저장된 파일 객체의 end point 입니다.
     *               https://{bucketName}.s3.{region}.amazonaws.com/{directory}/{fileName}
     *               위 구조로 되어있는데, file name이 한글이면 utf-8로 인코딩되어 url이 만들어지기 때문에 디코딩 과정이 필요하여 넣었습니다.  
     */
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
