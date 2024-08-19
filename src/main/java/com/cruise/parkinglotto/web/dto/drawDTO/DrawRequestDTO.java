package com.cruise.parkinglotto.web.dto.drawDTO;

import com.cruise.parkinglotto.domain.enums.DrawType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class DrawRequestDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetCurrentDrawInfoDTO {
        @NotNull
        private Long drawId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDrawRequestDTO {
        @NotNull(message = "추첨 시작시간을 입력해주세요.")
        private LocalDateTime drawStartAt;

        @NotNull(message = "추첨 종료시간을 입력해주세요.")
        private LocalDateTime drawEndAt;

        @NotNull(message = "주차공간 사용 시작시간을 입력해주세요.")
        private LocalDateTime usageStartAt;

        @NotNull(message = "주차공간 사용 종료시간을 입력해주세요.")
        private LocalDateTime usageEndAt;

        private String description;

        @NotNull(message = "추첨 유형을 입력해주세요.")
        private DrawType type;
    }
}
