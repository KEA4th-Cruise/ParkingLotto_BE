package com.cruise.parkinglotto.web.controller;

import com.cruise.parkinglotto.global.response.ApiResponse;
import com.cruise.parkinglotto.global.response.code.status.SuccessStatus;
import com.cruise.parkinglotto.service.WeightDetailService.WeightDetailService;
import com.cruise.parkinglotto.service.drawService.DrawService;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;
import com.cruise.parkinglotto.web.dto.weightDetailDTO.WeightDetailResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weight-detail")
@RequiredArgsConstructor
public class WeightDetailRestController {
    private final WeightDetailService weightDetailService;

    //가중치 정보 조회 API
    @GetMapping("/weight-calculate")
    public ApiResponse<WeightDetailResponseDTO.GetMemberWeightDTO> calculateMemberWeight(HttpServletRequest httpServletRequest) {
        WeightDetailResponseDTO.GetMemberWeightDTO calculateMemberWeightResponseDTO = weightDetailService.getMemberWeight(httpServletRequest);
        return ApiResponse.onSuccess(SuccessStatus.WEIGHT_DETAIL_FOUND, calculateMemberWeightResponseDTO);
    }
}
