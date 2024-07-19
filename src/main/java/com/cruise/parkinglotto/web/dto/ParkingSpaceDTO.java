package com.cruise.parkinglotto.web.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


public class ParkingSpaceDTO {
    @Getter
    public static class ParkingSpaceInfoResponseDTO {
        private String floorPlanImageUrl;
        private String name;
        private String address;
        private String title;
        private LocalDateTime startAt;
        private LocalDateTime endAt;

        @Builder
        public ParkingSpaceInfoResponseDTO(String floorPlanImageUrl, String name, String address, String title, LocalDateTime startAt, LocalDateTime endAt) {
            this.floorPlanImageUrl = floorPlanImageUrl;
            this.name = name;
            this.address = address;
            this.title = title;
            this.startAt = startAt;
            this.endAt = endAt;
        }
    }
}
