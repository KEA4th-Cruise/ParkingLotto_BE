package com.cruise.parkinglotto.service.certificateDocsService;

import com.cruise.parkinglotto.domain.CertificateDocs;
import com.cruise.parkinglotto.domain.enums.DrawType;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.kc.ObjectStorageService;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.repository.CertificateDocsRepository;
import com.cruise.parkinglotto.web.dto.CertificateDocsDTO.CertificateDocsRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CertificateDocsServiceImpl implements CertificateDocsService {

    private final ObjectStorageService objectStorageService;
    private final CertificateDocsRepository certificateDocsRepository;

    // 100MB
    private static final long MAX_TOTAL_CERTIFICATE_FILE_SIZE = 104857600L;
    private static final Set<String> ALLOWED_CERTIFICATE_FILE_EXTENSIONS = Set.of(".pdf", ".docx", ".xlsx", ".hwp", ".jpg", ".png"); // 허용된 확장자 목록
    private static final long MAX_CERTIFICATE_FILE_LENGTH = 30L;

    @Override
    public void validateCertificateFiles(List<MultipartFile> certificateFiles) throws ExceptionHandler {
        long totalCertificateFileSize = 0L;
        for (MultipartFile certificateFile : certificateFiles) {
            String fileName = certificateFile.getOriginalFilename();

            //파일 이름 검증
            if (fileName == null) {
                throw new ExceptionHandler(ErrorStatus.FILE_NAME_NOT_FOUND);
            } else if (fileName.length() > MAX_CERTIFICATE_FILE_LENGTH) {
                throw new ExceptionHandler(ErrorStatus.FILE_NAME_TOO_LONG);
            } else {
                String fileExtension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
                totalCertificateFileSize += certificateFile.getSize();

                //확장자 검증
                if (!ALLOWED_CERTIFICATE_FILE_EXTENSIONS.contains(fileExtension)) {
                    throw new ExceptionHandler(ErrorStatus.FILE_FORMAT_NOT_SUPPORTED);
                }
            }
        }

        //모든 파일의 전체 크기 검증
        if (totalCertificateFileSize > MAX_TOTAL_CERTIFICATE_FILE_SIZE) {
            throw new ExceptionHandler(ErrorStatus.FILE_TOO_LARGE);
        }
    }

    @Override
    public void checkCertificateFileUrlsInBucket(List<CertificateDocsRequestDTO.CertificateFileDTO> certificateFileDTOs) throws ExceptionHandler {
        for (CertificateDocsRequestDTO.CertificateFileDTO fileDTO : certificateFileDTOs) {
            String fileUrl = fileDTO.getFileUrl();
            if (!objectStorageService.doesObjectCertificateFileUrlExist(fileUrl)) {
                throw new ExceptionHandler(ErrorStatus.APPLICANT_CERT_DOCUMENT_NOT_FOUND);
            }
        }
    }

    @Override
    public void deleteCertificateDocsInMySql(List<CertificateDocsRequestDTO.CertificateFileDTO> certificateFileDTOs) throws ExceptionHandler {
        for (CertificateDocsRequestDTO.CertificateFileDTO fileDTO : certificateFileDTOs) {
            String fileUrl = fileDTO.getFileUrl();
            certificateDocsRepository.deleteByFileUrl(fileUrl);
        }
    }

    @Override
    public void prohibitSameFileNamesBetweenProfileFileUrlsAndMultiPartFiles(List<MultipartFile> certificateFiles, List<CertificateDocsRequestDTO.CertificateFileDTO> certificateFileDTO) throws ExceptionHandler {
        for (MultipartFile multipartFile : certificateFiles) {
            for (CertificateDocsRequestDTO.CertificateFileDTO fileDTO : certificateFileDTO) {
                if (Objects.equals(multipartFile.getOriginalFilename(), fileDTO.getFileName())) {
                    throw new ExceptionHandler(ErrorStatus.FILE_NAME_DUPLICATED);
                }

            }
        }
    }

    @Override
    public String makeCertificateFileUrl(Long memberId, Long drawId, DrawType drawType, String fileName) {
        return memberId.toString() + '_' + drawId.toString() + '_' + drawType.toString() + '_' + fileName;
    }

    @Override
    public void deleteFileIsNotInProfile(List<CertificateDocs> certificateDocsList) {
        for (CertificateDocs certificateDocs : certificateDocsList) {
            String[] urlParts = certificateDocs.getFileUrl().split("/");
            String fileName = urlParts[7];
            if (fileName.length() > certificateDocs.getFileName().length() + 4) {
                objectStorageService.deleteObject(certificateDocs.getFileUrl());
            }
        }
    }

}
