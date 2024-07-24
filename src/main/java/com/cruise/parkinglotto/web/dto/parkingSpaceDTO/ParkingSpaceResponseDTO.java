package com.cruise.parkinglotto.web.dto.parkingSpaceDTO;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class ParkingSpaceResponseDTO {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AddParkingSpaceResultDTO {
        private String name;
        private Integer slots;
        private String floorPlanImageUrl;
        private String address;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetNameAndUrlParkingSpaceResultDTO {
        String name;
        String floorPlanImageUrl;
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

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ParkingSpacePreviewDTO{
        private String name;
        private String address;
        private Integer slots;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ParkingSpacePreviewListDTO{
        private List<ParkingSpacePreviewDTO> parkingSpacePreviewDTOList;
    }
}