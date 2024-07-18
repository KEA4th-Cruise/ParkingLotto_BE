package com.cruise.parkinglotto.web.dto;

import lombok.Builder;
import lombok.Getter;

public class ParkingSpaceDTO {
    @Getter
    public static class ParkingSpaceImgResponseDTO {
        private String floorPlanImageUrl;
        private String name;
        private String address;

        @Builder
        public ParkingSpaceImgResponseDTO(String floorPlanImageUrl, String name, String address) {
            this.floorPlanImageUrl = floorPlanImageUrl;
            this.name = name;
            this.address = address;
        }
    }
}
