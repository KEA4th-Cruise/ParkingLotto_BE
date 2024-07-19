package com.cruise.parkinglotto.web.dto.parkingSpaceDTO;

import lombok.*;

import java.time.LocalDateTime;

public class ParkingSpaceResponseDTO {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AddParkingSpaceResultDTO {
        private String name;
        private Long slots;
        private String floorPlanImageUrl;
        private String address;
    }

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
