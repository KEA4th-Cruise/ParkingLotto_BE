package com.cruise.parkinglotto.web.controller;

import com.cruise.parkinglotto.global.response.ApiResponse;
import com.cruise.parkinglotto.global.response.code.status.SuccessStatus;
import com.cruise.parkinglotto.service.WeightDetailService.WeightDetailService;
import com.cruise.parkinglotto.web.dto.weightDetailDTO.WeightDetailRequestDTO;
import com.cruise.parkinglotto.web.dto.weightDetailDTO.WeightDetailResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weight")
@RequiredArgsConstructor
public class WeightRestController {
    private final WeightDetailService weightDetailService;

    //가중치 정보 조회 API
    @GetMapping("/detail")
    public ApiResponse<WeightDetailResponseDTO.GetMemberWeightDTO> getMemberWeight(HttpServletRequest httpServletRequest) {
        WeightDetailResponseDTO.GetMemberWeightDTO calculateMemberWeightResponseDTO = weightDetailService.getMemberWeight(httpServletRequest);
        return ApiResponse.onSuccess(SuccessStatus.WEIGHT_DETAIL_FOUND, calculateMemberWeightResponseDTO);
    }

    //가중치 계산 API
    @GetMapping("/calculate")
    public ApiResponse<WeightDetailResponseDTO.CalculateWeightResponseDTO> calculateWeight(@Valid @RequestBody WeightDetailRequestDTO.CalculateWeightRequestDTO calculateWeightRequestDTO){
        WeightDetailResponseDTO.CalculateWeightResponseDTO calculateWeightResponseDTO = weightDetailService.calculateWeight(calculateWeightRequestDTO);
        return ApiResponse.onSuccess(SuccessStatus.CALCULATE_MEMBER_WEIGHT_COMPLETED, calculateWeightResponseDTO);
    }
}