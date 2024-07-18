package com.cruise.parkinglotto.web.dto.drawDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DrawRequestDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetCurrentDrawInfo{
        private long drawId;
        private long parkingId;
    }
}
