package com.cruise.parkinglotto.web.controller;

import com.cruise.parkinglotto.global.response.ApiResponse;
import com.cruise.parkinglotto.global.response.code.status.SuccessStatus;
import com.cruise.parkinglotto.service.parkingSpaceService.ParkingSpaceServiceImpl;
import com.cruise.parkinglotto.web.dto.ParkingSpaceDTO.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/parkingspace")
@RequiredArgsConstructor
public class ParkingSpaceController {

    private final ParkingSpaceServiceImpl parkingSpaceService;

    @GetMapping("/myspace/{memberId}")
    public ApiResponse<ParkingSpaceInfoResponseDTO> getParkingSpaceImg(@PathVariable Long memberId) {

        return ApiResponse.onSuccess(SuccessStatus.PARKING_SPACE_INFO_FOUND,parkingSpaceService.findParkingSpaceImg(memberId));
    }

}
