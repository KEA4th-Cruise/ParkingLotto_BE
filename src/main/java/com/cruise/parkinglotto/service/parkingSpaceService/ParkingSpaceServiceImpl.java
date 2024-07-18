package com.cruise.parkinglotto.service.parkingSpaceService;

import com.cruise.parkinglotto.converter.ParkingSpaceConverter;
import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.global.aws.AmazonConfig;
import com.cruise.parkinglotto.global.aws.AmazonS3Manager;
import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.repository.DrawRepository;
import com.cruise.parkinglotto.repository.ParkingSpaceRepository;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ParkingSpaceServiceImpl implements ParkingSpaceService {

    private final ParkingSpaceRepository parkingSpaceRepository;
    private final DrawRepository drawRepository;
    private final AmazonS3Manager amazonS3Manager;
    private final AmazonConfig amazonConfig;

    @Override
    @Transactional
    public ParkingSpace addParkingSpace(Long drawId, MultipartFile floorPlanImage, ParkingSpaceRequestDTO.AddParkingSpaceDTO addParkingSpaceDTO) {
        Draw draw = drawRepository.findById(drawId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.DRAW_NOT_FOUND));
        String drawTitle = draw.getTitle().replace(" ", "_");
        String parkingSpaceName = addParkingSpaceDTO.getName().replace(" ", "_");
        String floorPlanImageUrl = amazonS3Manager.uploadFileToDirectory(
                amazonConfig.getParkingSpaceImagePath(),
                drawTitle + "_" + parkingSpaceName,
                floorPlanImage);
        ParkingSpace parkingSpace = ParkingSpaceConverter.toParkingSpace(addParkingSpaceDTO, floorPlanImageUrl, draw);
        return parkingSpaceRepository.save(parkingSpace);
    }
}
