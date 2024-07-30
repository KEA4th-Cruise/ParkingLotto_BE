package com.cruise.parkinglotto.web.controller;

import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.global.jwt.JwtUtils;
import com.cruise.parkinglotto.global.response.ApiResponse;
import com.cruise.parkinglotto.global.response.code.status.SuccessStatus;
import com.cruise.parkinglotto.service.memberService.MemberService;
import com.cruise.parkinglotto.service.parkingSpaceService.ParkingSpaceService;
import com.cruise.parkinglotto.web.converter.ParkingSpaceConverter;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceRequestDTO;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/parking-space")
@RequiredArgsConstructor
public class ParkingSpaceRestController {
    private final ParkingSpaceService parkingSpaceService;
    private final JwtUtils jwtUtils;
    private final MemberService memberService;

    @PostMapping("/{drawId}")
    public ApiResponse<ParkingSpaceResponseDTO.AddParkingSpaceResultDTO> addParkingSpace(@PathVariable Long drawId,
                                                                                         @RequestPart(value = "floorPlanImage", required = true) MultipartFile floorPlanImage,
                                                                                         @RequestPart(value = "addParkingSpaceDTO", required = true) ParkingSpaceRequestDTO.AddParkingSpaceDTO addParkingSpaceDTO) {
        ParkingSpace parkingSpace = parkingSpaceService.addParkingSpace(drawId, floorPlanImage, addParkingSpaceDTO);
        return ApiResponse.onSuccess(SuccessStatus.PARKING_SPACE_ADDED, ParkingSpaceConverter.toAddParkingSpaceResultDTO(parkingSpace));
    }

    @GetMapping("/{drawId}/my-space")
    public ApiResponse<ParkingSpaceResponseDTO.ParkingSpaceInfoResponseDTO> getParkingSpaceInfo(@PathVariable("drawId") Long drawId, HttpServletRequest httpServletRequest) {

        String accountIdFromRequest = jwtUtils.getAccountIdFromRequest(httpServletRequest);
        Long memberId = memberService.getMemberIdByAccountId(accountIdFromRequest);

        return ApiResponse.onSuccess(SuccessStatus.PARKING_SPACE_INFO_FOUND, parkingSpaceService.findParkingSpaceInfo(memberId, drawId));
    }

}
