package com.cruise.parkinglotto.service.drawService;

import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.repository.DrawRepository;
import com.cruise.parkinglotto.repository.ParkingSpaceRepository;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawRequestDTO;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.cruise.parkinglotto.web.converter.DrawConverter.toGetCurrentDrawInfo;

@Service
@RequiredArgsConstructor
public class DrawServiceImpl implements DrawService {
    private final DrawRepository drawRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;



    @Override
    @Transactional(readOnly = true)
    public DrawResponseDTO.GetCurrentDrawInfoDTO getCurrentDrawInfo(HttpServletRequest httpServletRequest, DrawRequestDTO.GetCurrentDrawInfoDTO getCurrentDrawInfo) {
        Draw draw = drawRepository.findById(getCurrentDrawInfo.getDrawId())
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));

        List<ParkingSpace> parkingSpace = parkingSpaceRepository.findByDrawId(draw.getId());
        if (parkingSpace == null) {
            throw new ExceptionHandler(ErrorStatus.PARKING_SPACE_NOT_FOUND);
        }
        List<String> floorPlanImageUrls = parkingSpace.stream()
                .map(ParkingSpace::getFloorPlanImageUrl)
                .toList();

        return toGetCurrentDrawInfo(draw, floorPlanImageUrls);
    }
}
