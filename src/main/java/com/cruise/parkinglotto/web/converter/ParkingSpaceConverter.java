package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class ParkingSpaceConverter {

    public static List<ParkingSpaceResponseDTO.GetNameAndIdWhichFromIsParkingResponseDTO> toGetNameAndIdWhichFromIsParkingResponse(List<ParkingSpace> parkingSpaces) {
        if (parkingSpaces == null || parkingSpaces.isEmpty()) {
            throw new ExceptionHandler(ErrorStatus.PARKING_SPACE_NOT_FOUND);
        }

        return parkingSpaces.stream()
                .map(parkingSpace -> ParkingSpaceResponseDTO.GetNameAndIdWhichFromIsParkingResponseDTO.builder()
                        .name(parkingSpace.getName())
                        .floorPlanImageUrl(parkingSpace.getFloorPlanImageUrl())
                        .build())
                .collect(Collectors.toList());
    }
}
