package com.cruise.parkinglotto.web.dto.weightDetailDTO;

import com.cruise.parkinglotto.domain.enums.DrawType;
import com.cruise.parkinglotto.domain.enums.WorkType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class WeightDetailRequestDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CalculateWeightRequestDTO {
        @NotNull(message = "근무타입을 선택해주세요.")
        private WorkType workType;

        @NotNull(message = "직선거리가 계산되지 않았습니다.")
        private Double distance;

        @NotNull(message = "대중교통 통근시간이 계산되지 않았습니다.")
        private Integer trafficCommuteTime;

        @NotNull(message = "자가용 통근시간이 계산되지 않았습니다.")
        private Integer carCommuteTime;

        private Integer recentLossCount;
    }
}
