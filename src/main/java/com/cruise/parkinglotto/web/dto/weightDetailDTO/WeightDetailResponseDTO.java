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
        private int carCommuteTime;
        private int trafficCommuteTime;
        private double distance;
        private int recentLossCount;
        private String address;
        private int difference;
        private WorkType workType;
    }
}
