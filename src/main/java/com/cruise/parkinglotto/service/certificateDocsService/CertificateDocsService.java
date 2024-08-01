package com.cruise.parkinglotto.service.certificateDocsService;

import com.cruise.parkinglotto.domain.enums.DrawType;
import com.cruise.parkinglotto.web.dto.CertificateDocsDTO.CertificateDocsRequestDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CertificateDocsService {

    void validateCertificateFiles(List<MultipartFile> files);

    void checkCertificateFileUrlsInBucket(List<CertificateDocsRequestDTO.CertificateFileDTO> certifiCateFileDTO);

    void deleteCertificateDocsInMySql(List<CertificateDocsRequestDTO.CertificateFileDTO> certifiCateFileDTO);

    void prohibitSameFileNamesBetweenProfileFileUrlsAndMultiPartFiles(List<MultipartFile> certificateFiles, List<CertificateDocsRequestDTO.CertificateFileDTO> certifiCateFileDTO);

    String makeCertificateFileUrl(Long memberId, Long drawId, DrawType drawType, String fileName);
}
