package com.cruise.parkinglotto.web.dto.applicantDTO;

import com.cruise.parkinglotto.domain.enums.DrawType;
import com.cruise.parkinglotto.domain.enums.WorkType;
import com.cruise.parkinglotto.web.dto.CertificateDocsDTO.CertificateDocsRequestDTO;
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
    public static class ApplyDrawRequestDTO {
        @NotNull
        private Long drawId;
        @NotNull
        private DrawType drawType;

        private String carNum;
        private List<CertificateDocsRequestDTO.CertifiCateFileDTO> getCertFileUrlAndNameDTO;
        private String address;
        private Integer trafficCommuteTime;
        private Integer carCommuteTime;
        private Double distance;
        private WorkType workType;
        private String userSeed;
        private Long firstChoice;
        private Long secondChoice;
    }

}
