package com.cruise.parkinglotto.service.parkingSpaceService;

import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceRequestDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ParkingSpaceService {
    ParkingSpace addParkingSpace(Long drawId,
                                 MultipartFile floorPlanImage,
                                 ParkingSpaceRequestDTO.AddParkingSpaceDTO addParkingSpaceDto);
}
