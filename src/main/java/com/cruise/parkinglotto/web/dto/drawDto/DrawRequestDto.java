package com.cruise.parkinglotto.web.dto.drawDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class DrawRequestDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetCurrentDrawInfo{
        private long drawId;
        private long parkingId;
    }
}
