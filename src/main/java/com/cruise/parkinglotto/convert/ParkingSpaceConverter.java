package com.cruise.parkinglotto.convert;

import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceResponseDTO;

public class ParkingSpaceConverter {
    public static ParkingSpaceResponseDTO.ParkingSpaceInfoResponseDTO toParkingSpaceInfoResponseDTO(ParkingSpace parkingSpace) {

        return ParkingSpaceResponseDTO.ParkingSpaceInfoResponseDTO.builder()
                .address(parkingSpace.getAddress())
                .floorPlanImageUrl(parkingSpace.getFloorPlanImageUrl())
                .name(parkingSpace.getName())
                .title(parkingSpace.getDraw().getTitle())
                .startAt(parkingSpace.getDraw().getUsageStartAt())
                .mapImageUrl(parkingSpace.getDraw().getMapImageUrl())
                .endAt(parkingSpace.getDraw().getUsageEndAt()).build();
    }
}
