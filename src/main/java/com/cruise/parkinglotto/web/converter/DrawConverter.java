package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.domain.enums.DrawStatus;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawRequestDTO;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceResponseDTO;
import lombok.RequiredArgsConstructor;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;

import java.util.List;


@RequiredArgsConstructor
public class DrawConverter {
    public static DrawResponseDTO.GetCurrentDrawInfoDTO toGetCurrentDrawInfo(Draw draw, List<ParkingSpace> parkingSpace) {

        List<ParkingSpaceResponseDTO.GetNameAndUrlParkingSpaceResultDTO> dto = ParkingSpaceConverter.toGetNameAndUrlParkingResponse(parkingSpace);

        return DrawResponseDTO.GetCurrentDrawInfoDTO.builder()
                .drawStartAt(draw.getDrawStartAt())
                .drawEndAt(draw.getDrawEndAt())
                .usageStartAt(draw.getUsageStartAt())
                .usageEndAt(draw.getUsageEndAt())
                .mapImageUrl(draw.getMapImageUrl())
                .getNameAndUrlParkingSpaceResultDTO(dto)
                .build();
    }

    public static Draw toDraw(DrawRequestDTO.CreateDrawRequestDTO createDrawRequestDTO, String title, String mapImageUrl, String year, String quarter) {
        return Draw.builder()
                .title(title)
                .type(createDrawRequestDTO.getType())
                .drawStartAt(createDrawRequestDTO.getDrawStartAt())
                .drawEndAt(createDrawRequestDTO.getDrawEndAt())
                .usageStartAt(createDrawRequestDTO.getUsageStartAt())
                .usageEndAt(createDrawRequestDTO.getUsageEndAt())
                .mapImageUrl(mapImageUrl)
                .description(createDrawRequestDTO.getDescription())
                .year(year)
                .quarter(quarter)
                .status(DrawStatus.PENDING)
                .totalSlots(0L)
                .confirmed(false)
                .build();
    }

    public static DrawResponseDTO.CreateDrawResultDTO toCreateDrawResultDTO(Draw draw) {
        return DrawResponseDTO.CreateDrawResultDTO.builder()
                .drawId(draw.getId())
                .title(draw.getTitle())
                .createdAt(draw.getCreatedAt())
                .build();
    }

    public static DrawResponseDTO.ConfirmDrawCreationResultDTO toConfirmDrawCreationResultDTO(Draw draw, List<ParkingSpace> parkingSpaceList) {
        return DrawResponseDTO.ConfirmDrawCreationResultDTO.builder()
                .drawId(draw.getId())
                .title(draw.getTitle())
                .usageStartAt(draw.getUsageStartAt())
                .usageEndAt(draw.getUsageEndAt())
                .parkingSpacePreviewListDTO(ParkingSpaceConverter.toParkingSpacePreviewListDTO(parkingSpaceList))
                .build();
    }

}
