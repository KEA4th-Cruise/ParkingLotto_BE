package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class ParkingSpaceConverter {

    public static List<ParkingSpaceResponseDTO.GetNameAndUrlParkingSpaceResultDTO> toGetNameAndIdWhichFromIsParkingResponse(List<ParkingSpace> parkingSpaces) {

        return parkingSpaces.stream()
                .map(parkingSpace -> ParkingSpaceResponseDTO.GetNameAndUrlParkingSpaceResultDTO.builder()
                        .name(parkingSpace.getName())
                        .floorPlanImageUrl(parkingSpace.getFloorPlanImageUrl())
                        .build())
                .collect(Collectors.toList());
    }
}
