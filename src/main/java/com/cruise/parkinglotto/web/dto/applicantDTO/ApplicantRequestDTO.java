package com.cruise.parkinglotto.web.dto.applicantDTO;

import com.cruise.parkinglotto.domain.enums.WorkType;
import com.cruise.parkinglotto.web.dto.certificateDocsDTO.CertificateDocsRequestDTO;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.annotations.NotNull;

import java.util.List;

public class ApplicantRequestDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeneralApplyDrawRequestDTO {
        @NotNull
        @Pattern(regexp = "^[가-힣0-9]{8}$", message = "한글, 숫자포함 8자로 입력해주세요.")
        private String carNum;
        private List<CertificateDocsRequestDTO.CertificateFileDTO> useProfileFileUrlDTO;
        @NotNull
        private String address;
        @NotNull
        private Integer trafficCommuteTime;
        @NotNull
        private Integer carCommuteTime;
        @NotNull
        private Double distance;
        @NotNull
        private WorkType workType;
        @NotNull
        private String userSeed;
        @NotNull
        private Long firstChoice;
        @NotNull
        private Long secondChoice;
    }

}
