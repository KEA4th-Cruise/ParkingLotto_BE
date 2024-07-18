package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.ParkingSpace;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;
import java.time.LocalDateTime;


@RequiredArgsConstructor
public class DrawConverter {
    public static DrawResponseDTO.GetCurrentDrawInfo toGetCurrentDrawInfo(Draw draw, ParkingSpace parkingSpace) {
        return DrawResponseDTO.GetCurrentDrawInfo.builder()
                .drawStartAt(draw.getDrawStartAt())
                .drawEndAt(draw.getDrawEndAt())
                .usageStartAt(draw.getUsageStartAt())
                .usageEndAt(draw.getUsageEndAt())
                .mapImageUrl(draw.getMapImageUrl())
                .floorPlanImageUrl(parkingSpace.getFloorPlanImageUrl())
                .build();
        }
}
