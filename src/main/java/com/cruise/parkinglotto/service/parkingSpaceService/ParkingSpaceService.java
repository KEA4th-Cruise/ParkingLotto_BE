package com.cruise.parkinglotto.service.parkingSpaceService;

import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceRequestDTO;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ParkingSpaceService {
    ParkingSpace addParkingSpace(Long drawId,
                                 MultipartFile floorPlanImage,
                                 ParkingSpaceRequestDTO.AddParkingSpaceDTO addParkingSpaceDto);

    ParkingSpaceResponseDTO.ParkingSpaceInfoResponseDTO findParkingSpaceInfo(Long memberId);
}
