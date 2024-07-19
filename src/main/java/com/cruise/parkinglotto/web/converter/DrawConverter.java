package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;


@RequiredArgsConstructor
public class DrawConverter {
    public static DrawResponseDTO.GetCurrentDrawInfoDTO toGetCurrentDrawInfo(Draw draw, List<ParkingSpace> parkingSpace) {

        if (parkingSpace == null) {
            throw new ExceptionHandler(ErrorStatus.PARKING_SPACE_NOT_FOUND);
        }

        List<String> name = parkingSpace.stream()
                .map(ParkingSpace::getName)
                .toList();

        List<String> floorPlanImageUrls = parkingSpace.stream()
                .map(ParkingSpace::getFloorPlanImageUrl)
                .toList();


        return DrawResponseDTO.GetCurrentDrawInfoDTO.builder()
                .drawStartAt(draw.getDrawStartAt())
                .drawEndAt(draw.getDrawEndAt())
                .usageStartAt(draw.getUsageStartAt())
                .usageEndAt(draw.getUsageEndAt())
                .mapImageUrl(draw.getMapImageUrl())
                .name(name)
                .floorPlanImageUrl(floorPlanImageUrls)
                .build();
        }
}
