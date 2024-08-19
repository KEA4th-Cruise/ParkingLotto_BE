package com.cruise.parkinglotto.web.dto.certificateDocsDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CertificateDocsRequestDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CertificateFileDTO {
        private String fileUrl;
        private String fileName;
    }
}
