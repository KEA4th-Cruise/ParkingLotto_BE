package com.cruise.parkinglotto.web.dto.weightDetailDTO;

import com.cruise.parkinglotto.domain.enums.WorkType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class WeightDetailResponseDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetMemberWeightDTO {
        private Integer carCommuteTime;
        private Integer trafficCommuteTime;
        private Double distance;
        private Integer recentLossCount;
        private String address;
        private Integer difference;
        private WorkType workType;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CalculateWeightResponseDTO {
        private Double calculateResult;
    }
}
