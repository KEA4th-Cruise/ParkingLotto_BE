package com.cruise.parkinglotto.web.converter;

import java.util.List;
import java.util.stream.Collectors;
import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceRequestDTO;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceResponseDTO;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParkingSpaceConverter {

    public static List<ParkingSpaceResponseDTO.GetNameAndUrlParkingSpaceResultDTO> toGetNameAndUrlParkingResponse(List<ParkingSpace> parkingSpaces) {
        return parkingSpaces.stream()
                .map(parkingSpace -> ParkingSpaceResponseDTO.GetNameAndUrlParkingSpaceResultDTO.builder()
                        .name(parkingSpace.getName())
                        .floorPlanImageUrl(parkingSpace.getFloorPlanImageUrl())
                        .build())
                .collect(Collectors.toList());
    }

    public static ParkingSpace toParkingSpace(ParkingSpaceRequestDTO.AddParkingSpaceDTO addParkingSpaceDto, String floorPlanImageUrl, Draw draw) {
        return ParkingSpace.builder()
                .address(addParkingSpaceDto.getAddress())
                .name(addParkingSpaceDto.getName())
                .slots(addParkingSpaceDto.getSlots())
                .remainSlots(addParkingSpaceDto.getSlots())
                .floorPlanImageUrl(floorPlanImageUrl)
                .applicantCount(0L)
                .confirmed(false)
                .draw(draw).build();
    }

    public static ParkingSpaceResponseDTO.AddParkingSpaceResultDTO toAddParkingSpaceResultDTO(ParkingSpace parkingSpace) {
        return ParkingSpaceResponseDTO.AddParkingSpaceResultDTO.builder()
                .name(parkingSpace.getName())
                .slots(parkingSpace.getSlots())
                .floorPlanImageUrl(parkingSpace.getFloorPlanImageUrl())
                .address(parkingSpace.getAddress())
                .build();
    }

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
