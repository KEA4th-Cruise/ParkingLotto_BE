package com.cruise.parkinglotto.web.dto;

import lombok.Builder;
import lombok.Getter;

public class ParkingSpaceDTO {
    @Getter
    public static class ParkingSpaceImgResponseDTO {
        private String floorPlanImageUrl;

        @Builder
        public ParkingSpaceImgResponseDTO(String floorPlanImageUrl) {
            this.floorPlanImageUrl = floorPlanImageUrl;
        }

    }
}
