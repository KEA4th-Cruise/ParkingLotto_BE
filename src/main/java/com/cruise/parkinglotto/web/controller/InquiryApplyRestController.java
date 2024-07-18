package com.cruise.parkinglotto.web.controller;


import com.cruise.parkinglotto.global.response.ApiResponse;
import com.cruise.parkinglotto.global.response.code.status.ErrorStatus;
import com.cruise.parkinglotto.service.DrawCommandService;
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
@RequestMapping("/InquiryApply")
@RequiredArgsConstructor
public class InquiryApplyRestController {
    private final DrawCommandService drawCommandService;

    @GetMapping("/GetCurrentDrawInfo")
    public ApiResponse<DrawResponseDTO.GetCurrentDrawInfo> getHistory(HttpServletRequest httpServletRequest,
                                                                      @Valid @RequestBody DrawRequestDTO.GetCurrentDrawInfo reqeust) {

        try {
            DrawResponseDTO.GetCurrentDrawInfo getCurrentDrawInfoDto = drawCommandService.getCurrentDrawInfo(httpServletRequest, reqeust);
            return ApiResponse.onSuccess(SuccessStatus._OK, getCurrentDrawInfoDto);
        } catch (RuntimeException e) {
            return ApiResponse.onFailure(ErrorStatus._BAD_REQUEST.getCode(), e.getMessage(), null);
        }
    }
}
