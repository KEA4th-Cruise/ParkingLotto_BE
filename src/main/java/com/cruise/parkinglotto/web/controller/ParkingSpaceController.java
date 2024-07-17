package com.cruise.parkinglotto.web.controller;

import com.cruise.parkinglotto.service.ParkingSpaceService;
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

    private final ParkingSpaceService parkingSpaceService;

    @GetMapping("/myspace/{memberId}")
    public ParkingSpaceImgResponseDTO getParkingSpaceImg(@PathVariable Long memberId) {
        return parkingSpaceService.findParkingSpaceImg(memberId);
    }

}
