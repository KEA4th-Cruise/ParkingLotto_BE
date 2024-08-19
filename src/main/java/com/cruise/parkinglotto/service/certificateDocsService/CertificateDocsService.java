package com.cruise.parkinglotto.service.certificateDocsService;

import com.cruise.parkinglotto.domain.CertificateDocs;
import com.cruise.parkinglotto.domain.enums.DrawType;
import com.cruise.parkinglotto.web.dto.certificateDocsDTO.CertificateDocsRequestDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CertificateDocsService {

    void validateCertificateFiles(List<MultipartFile> files);

    String makeCertificateFileUrl(Long memberId, Long drawId, DrawType drawType, String fileName);

    void deleteFileIsNotInProfile(List<CertificateDocs> certificateDocsList);
}
