package com.cruise.parkinglotto.web.dto.parkingSpaceDTO;

import lombok.*;

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
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetNameAndUrlParkingSpaceResultDTO {
        String name;
        String floorPlanImageUrl;
    }
}
