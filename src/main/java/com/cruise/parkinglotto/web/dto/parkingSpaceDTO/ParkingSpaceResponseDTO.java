package com.cruise.parkinglotto.web.dto.parkingSpaceDTO;

import lombok.*;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.time.LocalDateTime;
import java.util.Iterator;
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
    public static class ParkingSpacePreviewDTO {
        private String name;
        private String address;
        private Integer slots;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ParkingSpacePreviewListDTO {
        private List<ParkingSpacePreviewDTO> parkingSpacePreviewDTOList;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ParkingSpaceCompetitionRateDTO {
        private Long parkingSpaceId;
        private String name;
        private Integer slots;
        private Integer applicantsCount;
    }
}
