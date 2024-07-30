package com.cruise.parkinglotto.service.parkingSpaceService;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.kc.ObjectStorageConfig;
import com.cruise.parkinglotto.global.kc.ObjectStorageService;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.repository.ApplicantRepository;
import com.cruise.parkinglotto.repository.DrawRepository;
import com.cruise.parkinglotto.repository.ParkingSpaceRepository;
import com.cruise.parkinglotto.web.converter.ParkingSpaceConverter;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceRequestDTO;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ParkingSpaceServiceImpl implements ParkingSpaceService {

    private final ParkingSpaceRepository parkingSpaceRepository;
    private final DrawRepository drawRepository;
    private final ApplicantRepository applicantRepository;
    private final ObjectStorageConfig objectStorageConfig;
    private final ObjectStorageService objectStorageService;

    @Override
    @Transactional
    public ParkingSpace addParkingSpace(Long drawId, MultipartFile floorPlanImage, ParkingSpaceRequestDTO.AddParkingSpaceDTO addParkingSpaceDTO) {
        Draw draw = drawRepository.findById(drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));

        String drawTitle = draw.getTitle().replace(" ", "_");
        String parkingSpaceName = addParkingSpaceDTO.getName().replace(" ", "_");
        String floorPlanImageUrl = objectStorageService.uploadObject(objectStorageConfig.getParkingSpaceImagePath(),

                drawTitle + "_" + parkingSpaceName,
                floorPlanImage);
        ParkingSpace parkingSpace = ParkingSpaceConverter.toParkingSpace(addParkingSpaceDTO, floorPlanImageUrl, draw);
        return parkingSpaceRepository.save(parkingSpace);
    }

    public ParkingSpaceResponseDTO.ParkingSpaceInfoResponseDTO findParkingSpaceInfo(Long memberId, Long drawId) {


        Applicant applicant = applicantRepository.findApplicantByMemberIdAndDrawId(memberId, drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.APPLICANT_NOT_FOUND));
        Long parkingSpaceId = applicantRepository.findParkingSpaceById(applicant.getId()).orElseThrow(() -> new ExceptionHandler(ErrorStatus.APPLICANT_PARKING_SPACE_ID_NOT_FOUND));
        ParkingSpace findParkingSpace = parkingSpaceRepository.findById(parkingSpaceId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.PARKING_SPACE_NOT_FOUND));

        return ParkingSpaceConverter.toParkingSpaceInfoResponseDTO(findParkingSpace);
    }
}