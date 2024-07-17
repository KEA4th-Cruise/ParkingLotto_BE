package com.cruise.parkinglotto.service;

import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.repository.DrawRepository;
import com.cruise.parkinglotto.repository.ParkingSpaceRepository;
import com.cruise.parkinglotto.web.dto.drawDto.DrawRequestDto;
import com.cruise.parkinglotto.web.dto.drawDto.DrawResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DrawCommandServiceImpl implements DrawCommandService{
    private final DrawRepository drawRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;

    @Override
    @Transactional(readOnly = true)
    public DrawResponseDto.GetCurrentDrawInfo getCurrentDrawInfo(HttpServletRequest httpServletRequest, DrawRequestDto.GetCurrentDrawInfo getCurrentDrawInfo){
        Optional<Draw> drawOptional = drawRepository.findById(getCurrentDrawInfo.getDrawId());
        Draw draw = drawOptional.orElseThrow(() -> new RuntimeException("DrawId Not Found: " + getCurrentDrawInfo.getDrawId()));

        Optional<ParkingSpace> parkingSpaceOptional = parkingSpaceRepository.findByIdAndDraw_Id(getCurrentDrawInfo.getParkingId(), draw.getId());
        ParkingSpace parkingSpace = parkingSpaceOptional.orElseThrow(() -> new RuntimeException("DrawId: "+ getCurrentDrawInfo.getDrawId()+" does not have ParkingSpaceId: " + getCurrentDrawInfo.getParkingId()));

        return DrawResponseDto.GetCurrentDrawInfo.builder()
                .drawStartAt(draw.getDrawStartAt())
                .drawEndAt(draw.getDrawEndAt())
                .usageStartAt(draw.getUsageStartAt())
                .usageEndAt(draw.getUsageEndAt())
                .mapImageUrl(draw.getMapImageUrl())
                .floorPlanImageUrl(parkingSpace.getFloorPlanImageUrl())
                .build();
    }
}
