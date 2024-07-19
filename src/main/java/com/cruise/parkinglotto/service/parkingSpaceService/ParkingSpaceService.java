package com.cruise.parkinglotto.service.parkingSpaceService;

import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceResponseDTO;

public interface ParkingSpaceService {

     ParkingSpaceResponseDTO.ParkingSpaceInfoResponseDTO findParkingSpaceInfo(Long memberId);

}
