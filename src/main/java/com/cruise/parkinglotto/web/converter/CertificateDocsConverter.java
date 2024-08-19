package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.domain.CertificateDocs;
import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.web.dto.certificateDocsDTO.CertificateDocsRequestDTO;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CertificateDocsConverter {

    public static CertificateDocs toCertificateDocument(String fileUrl, String fileName, Member member, Long drawId) {
        return CertificateDocs.builder()
                .fileUrl(fileUrl)
                .fileName(fileName)
                .member(member)
                .drawId(drawId)
                .build();
    }

    public static CertificateDocsRequestDTO.CertificateFileDTO toCertificateFileDTO(CertificateDocs certificateDocs) {
        return CertificateDocsRequestDTO.CertificateFileDTO.builder()
                .fileName(certificateDocs.getFileName())
                .fileUrl(certificateDocs.getFileUrl())
                .build();
    }
}
