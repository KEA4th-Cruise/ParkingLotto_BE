package com.cruise.parkinglotto.web.dto.applicantDTO;

import com.cruise.parkinglotto.domain.enums.WinningStatus;
import com.cruise.parkinglotto.domain.enums.WorkType;
import com.cruise.parkinglotto.web.dto.CertificateDocsDTO.CertificateDocsRequestDTO;
import com.cruise.parkinglotto.web.dto.CertificateDocsDTO.CertificateDocsResponseDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class ApplicantResponseDTO {
    @Builder
    @Data
    public static class ApplicantResultDTO {
        private Double weightedTotalScore;
        private WinningStatus winningStatus;
        private Integer reserveNum;
        private Integer userSeedIndex;
        private String userSeed;
        private String userName;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetApplicantResultDTO {
        private Long applicantId;
        private Long memberId;
        private String employeeNo;
        private String nameKo;
        private String deptPathName;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetApplicantListResultDTO {
        private List<GetApplicantResultDTO> applicantList;
        Integer listSize;
        Integer totalPage;
        Long totalElements;
        Boolean isFirst;
        Boolean isLast;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyApplyInfoDTO {
        private Long parkingSpaceId;
        private String drawTitle;
        private WinningStatus winningStatus;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class getMyApplyInformationDTO {
        String carNum;
        String address;
        WorkType workType;
        Long firstChoice;
        Long secondChoice;
        String userSeed;
        Integer recentLossCount;
        List<CertificateDocsRequestDTO.CertificateFileDTO> certificateFiles;
    }

}
