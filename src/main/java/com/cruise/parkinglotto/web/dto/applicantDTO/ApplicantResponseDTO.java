package com.cruise.parkinglotto.web.dto.applicantDTO;

import com.cruise.parkinglotto.domain.enums.WinningStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ApplicantResponseDTO {
    @Builder
    @Data
    public static class ApplicantResultDTO {
        private Double weightedTotalScore;
        private WinningStatus winningStatus;
        private String parkingSpaceName;
        private Integer reserveNum;
        private Integer userSeedIndex;
        private String userSeed;
        private String firstChoice;
        private String secondChoice;
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
        private List<GetApplicantResultDTO> getApplicantResultDTOList;
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
    public static class ApprovePriorityResultDTO {
        private Long parkingSpaceId;
        private String parkingSpaceName;
        private Integer remainSlots;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyApplyInfoDTO {
        private Long parkingSpaceId;
        private String drawTitle;
        private WinningStatus winningStatus;
        private String parkingSpaceName;
        private String parkingSpaceAddress;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
    }

}
