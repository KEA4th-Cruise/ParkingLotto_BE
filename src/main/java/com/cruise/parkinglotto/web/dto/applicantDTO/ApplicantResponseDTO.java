package com.cruise.parkinglotto.web.dto.applicantDTO;

import com.cruise.parkinglotto.domain.enums.WinningStatus;
import lombok.Builder;
import lombok.Data;

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
}
