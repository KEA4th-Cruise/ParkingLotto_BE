package com.cruise.parkinglotto.web.dto.parkingSpaceDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


public class ParkingSpaceResponseDTO {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ParkingSpaceInfoResponseDTO {
        private String floorPlanImageUrl;
        private String mapImageUrl;
        private String name;
        private String address;
        private String title;
        private LocalDateTime startAt;
        private LocalDateTime endAt;

    }
}
