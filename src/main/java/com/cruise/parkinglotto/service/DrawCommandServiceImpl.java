package com.cruise.parkinglotto.service;

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

import java.util.Optional;

import static com.cruise.parkinglotto.web.converter.DrawConverter.toGetCurrentDrawInfo;

@Service
@RequiredArgsConstructor
public class DrawCommandServiceImpl implements DrawCommandService{
    private final DrawRepository drawRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;



    @Override
    @Transactional(readOnly = true)
    public DrawResponseDTO.GetCurrentDrawInfo getCurrentDrawInfo(HttpServletRequest httpServletRequest, DrawRequestDTO.GetCurrentDrawInfo getCurrentDrawInfo){
        Optional<Draw> drawOptional = drawRepository.findById(getCurrentDrawInfo.getDrawId());
        Draw draw = drawOptional.orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));

        Optional<ParkingSpace> parkingSpaceOptional = parkingSpaceRepository.findByIdAndDraw_Id(getCurrentDrawInfo.getParkingId(), draw.getId());
        ParkingSpace parkingSpace = parkingSpaceOptional.orElseThrow(() -> new ExceptionHandler(ErrorStatus.PARKING_SPACE_NOT_FOUND));

        return toGetCurrentDrawInfo(draw, parkingSpace);
    }
}
