package com.cruise.parkinglotto.service.parkingSpaceService;

import com.cruise.parkinglotto.convert.ParkingSpaceConverter;
import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.repository.ApplicantRepository;
import com.cruise.parkinglotto.repository.ParkingSpaceRepository;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceResponseDTO.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ParkingSpaceServiceImpl implements ParkingSpaceService {

    private final ParkingSpaceRepository parkingSpaceRepository;
    private final ApplicantRepository applicantRepository;

    public ParkingSpaceInfoResponseDTO findParkingSpaceInfo(Long memberId) {


        Long applicantId = applicantRepository.findByMember(memberId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.APPLICANT_NOT_FOUND));
        Long parkingSpaceId = applicantRepository.findParkingSpaceId(applicantId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.PARKING_SPACE_NOT_FOUND));
        ParkingSpace findParkingSpace = parkingSpaceRepository.findById(parkingSpaceId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.PARKING_SPACE_NOT_FOUND));

        return ParkingSpaceConverter.toParkingSpaceInfoResponseDTO(findParkingSpace);
    }


}
