package com.cruise.parkinglotto.service.parkingSpaceService;

import com.cruise.parkinglotto.web.dto.ParkingSpaceDTO;

public interface ParkingSpaceService {

    public ParkingSpaceDTO.ParkingSpaceInfoResponseDTO findParkingSpaceImg(Long memberId);

}
