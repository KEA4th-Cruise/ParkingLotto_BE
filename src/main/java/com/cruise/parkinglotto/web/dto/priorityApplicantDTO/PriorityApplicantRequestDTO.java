package com.cruise.parkinglotto.web.dto.priorityApplicantDTO;

import com.cruise.parkinglotto.domain.enums.WorkType;
import com.cruise.parkinglotto.web.dto.CertificateDocsDTO.CertificateDocsRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.annotations.NotNull;

import java.util.List;

public class PriorityApplicantRequestDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriorityApplyDrawRequestDTO {
        @NotNull
        private String carNum;
        private List<CertificateDocsRequestDTO.CertificateFileDTO> useProfileFileUrlDTO;
    }
}
