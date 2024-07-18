package com.cruise.parkinglotto.convert;

import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.web.dto.ParkingSpaceDTO;

public class ParkingSpaceConverter {
    public static ParkingSpaceDTO.ParkingSpaceImgResponseDTO toParkingSpaceDTO(ParkingSpace parkingSpace) {

        return ParkingSpaceDTO.ParkingSpaceImgResponseDTO.builder( )
                .address(parkingSpace.getAddress())
                .floorPlanImageUrl(parkingSpace.getFloorPlanImageUrl( ))
                .name(parkingSpace.getName( )).build( );

    }
}
