package com.cruise.parkinglotto.web.controller;

import com.cruise.parkinglotto.global.response.ApiResponse;
import com.cruise.parkinglotto.global.response.code.status.SuccessStatus;
import com.cruise.parkinglotto.service.weightDetailService.WeightDetailService;
import com.cruise.parkinglotto.web.dto.weightDetailDTO.WeightDetailRequestDTO;
import com.cruise.parkinglotto.web.dto.weightDetailDTO.WeightDetailResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "가중치 정보 조회 API", description = "가중치 계산기를 사용할 때 쓰이는 API로 토큰으로 기존 가중치 정보를 조회하는 API입니다.(이정균)")
    @GetMapping("/detail")
    public ApiResponse<WeightDetailResponseDTO.GetMemberWeightDTO> getMemberWeight(HttpServletRequest httpServletRequest) {
        WeightDetailResponseDTO.GetMemberWeightDTO calculateMemberWeightResponseDTO = weightDetailService.getMemberWeight(httpServletRequest);
        return ApiResponse.onSuccess(SuccessStatus.WEIGHT_DETAIL_FOUND, calculateMemberWeightResponseDTO);
    }

    //가중치 계산 API
    @Operation(summary = "가중치 계산 API", description = "가중치 계산기를 사용할 때 쓰이는 API로 가중치 요소들을 JSON 형태로 받아 가중치 계산을 요청하는 API입니다.(이정균)")
    @GetMapping("/calculate")
    public ApiResponse<WeightDetailResponseDTO.CalculateWeightResponseDTO> calculateWeight(@Valid @RequestBody WeightDetailRequestDTO.CalculateWeightRequestDTO calculateWeightRequestDTO) {
        WeightDetailResponseDTO.CalculateWeightResponseDTO calculateWeightResponseDTO = weightDetailService.calculateWeight(calculateWeightRequestDTO);
        return ApiResponse.onSuccess(SuccessStatus.CALCULATE_MEMBER_WEIGHT_COMPLETED, calculateWeightResponseDTO);
    }
}
