package com.cruise.parkinglotto.service.certificateDocsService;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CertificateDocsService {

    void validateCertificateFiles(List<MultipartFile> files);
}
