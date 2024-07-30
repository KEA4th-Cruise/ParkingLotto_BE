package com.cruise.parkinglotto.service.certificateDocsService;

import com.cruise.parkinglotto.web.dto.CertificateDocsDTO.CertificateDocsRequestDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CertificateDocsService {

    void validateCertificateFiles(List<MultipartFile> files);

    void checkCertificateFileUrlsInBucket(List<CertificateDocsRequestDTO.CertifiCateFileDTO> certifiCateFileDTO);

    void deleteCertificateDocsInMySql(List<CertificateDocsRequestDTO.CertifiCateFileDTO> certifiCateFileDTO);
}
