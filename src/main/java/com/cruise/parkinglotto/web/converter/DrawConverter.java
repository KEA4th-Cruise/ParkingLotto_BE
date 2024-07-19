package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.ParkingSpace;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;


@RequiredArgsConstructor
public class DrawConverter {
    public static DrawResponseDTO.GetCurrentDrawInfoDTO toGetCurrentDrawInfo(Draw draw, List<String> name, List<String> floorPlanImageUrl) {
        return DrawResponseDTO.GetCurrentDrawInfoDTO.builder()
                .drawStartAt(draw.getDrawStartAt())
                .drawEndAt(draw.getDrawEndAt())
                .usageStartAt(draw.getUsageStartAt())
                .usageEndAt(draw.getUsageEndAt())
                .mapImageUrl(draw.getMapImageUrl())
                .name(name)
                .floorPlanImageUrl(floorPlanImageUrl)
                .build();
        }
}
