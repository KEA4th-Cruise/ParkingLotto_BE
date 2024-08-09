package com.cruise.parkinglotto.web.dto.priorityApplicantDTO;

import com.cruise.parkinglotto.web.dto.certificateDocsDTO.CertificateDocsRequestDTO;
import jakarta.validation.constraints.Pattern;
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
        @Pattern(regexp = "^[가-힣0-9]{8}$", message = "한글, 숫자포함 8자로 입력해주세요.")
        private String carNum;
        private List<CertificateDocsRequestDTO.CertificateFileDTO> useProfileFileUrlDTO;
    }
}
