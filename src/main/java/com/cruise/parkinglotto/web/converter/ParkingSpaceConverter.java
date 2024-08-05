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

    public static List<ParkingSpaceResponseDTO.GetParkingSpaceResultDTO> toGetNameAndUrlParkingResponse(List<ParkingSpace> parkingSpaces) {
        return parkingSpaces.stream()
                .map(parkingSpace -> ParkingSpaceResponseDTO.GetParkingSpaceResultDTO.builder()
                        .name(parkingSpace.getName())
                        .floorPlanImageUrl(parkingSpace.getFloorPlanImageUrl())
                        .slots(parkingSpace.getSlots())
                        .parkingSpaceAddress(parkingSpace.getAddress())
                        .build())
                .collect(Collectors.toList());
    }

    public static ParkingSpace toParkingSpace(ParkingSpaceRequestDTO.AddParkingSpaceDTO addParkingSpaceDTO, String floorPlanImageUrl, Draw draw) {
        return ParkingSpace.builder()
                .address(addParkingSpaceDTO.getAddress())
                .name(addParkingSpaceDTO.getName())
                .slots(addParkingSpaceDTO.getSlots())
                .remainSlots(addParkingSpaceDTO.getSlots())
                .floorPlanImageUrl(floorPlanImageUrl)
                .applicantCount(0)
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

    public static ParkingSpaceResponseDTO.ParkingSpacePreviewDTO toParkingSpacePreviewDTO(ParkingSpace parkingSpace) {
        return ParkingSpaceResponseDTO.ParkingSpacePreviewDTO.builder()
                .name(parkingSpace.getName())
                .address(parkingSpace.getAddress())
                .slots(parkingSpace.getSlots())
                .build();
    }

    public static ParkingSpaceResponseDTO.ParkingSpacePreviewListDTO toParkingSpacePreviewListDTO(List<ParkingSpace> parkingSpaceList) {
        List<ParkingSpaceResponseDTO.ParkingSpacePreviewDTO> parkingSpacePreviewDTOList = parkingSpaceList.stream()
                .map(ParkingSpaceConverter::toParkingSpacePreviewDTO).toList();
        return ParkingSpaceResponseDTO.ParkingSpacePreviewListDTO.builder()
                .parkingSpacePreviewDTOList(parkingSpacePreviewDTOList)
                .build();
    }

    public static ParkingSpaceResponseDTO.ParkingSpaceCompetitionRateDTO toParkingSpaceCompetitionRateDTO(ParkingSpace parkingSpace, Integer applicantsCount) {
        return ParkingSpaceResponseDTO.ParkingSpaceCompetitionRateDTO.builder()
                .parkingSpaceId(parkingSpace.getId())
                .applicantsCount(applicantsCount)
                .name(parkingSpace.getName())
                .slots(parkingSpace.getSlots())
                .build();
    }

}
