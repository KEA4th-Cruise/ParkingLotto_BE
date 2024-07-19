package com.cruise.parkinglotto.web.controller;


import com.cruise.parkinglotto.global.response.ApiResponse;
import com.cruise.parkinglotto.service.DrawService;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawRequestDTO;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import com.cruise.parkinglotto.global.response.code.status.SuccessStatus;

@Slf4j
@RestController
@RequestMapping("/inquiryApply")
@RequiredArgsConstructor
public class InquiryApplyRestController {
    private final DrawService drawService;

    @GetMapping("/getCurrentDrawInfo")
    public ApiResponse<DrawResponseDTO.GetCurrentDrawInfoDTO> getHistory(HttpServletRequest httpServletRequest,
                                                                      @Valid @RequestBody DrawRequestDTO.GetCurrentDrawInfoDTO request) {

        DrawResponseDTO.GetCurrentDrawInfoDTO getCurrentDrawInfoDto = drawService.getCurrentDrawInfo(httpServletRequest, request);

        return ApiResponse.onSuccess(SuccessStatus._OK, getCurrentDrawInfoDto);

    }
}
