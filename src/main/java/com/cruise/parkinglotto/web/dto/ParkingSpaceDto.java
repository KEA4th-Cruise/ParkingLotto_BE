package com.cruise.parkinglotto.web.dto;

import lombok.Builder;
import lombok.Getter;

public class ParkingSpaceDto {
    @Getter
    public static class ParkingSpaceImgResponseDto {
        private String floorPlanImageUrl;

        @Builder
        public ParkingSpaceImgResponseDto(String floorPlanImageUrl) {
            this.floorPlanImageUrl = floorPlanImageUrl;
        }

    }
}
