package com.cruise.parkinglotto.web.controller;

import com.cruise.parkinglotto.global.response.ApiResponse;
import com.cruise.parkinglotto.global.response.code.status.SuccessStatus;
import com.cruise.parkinglotto.service.drawService.DrawService;
import com.cruise.parkinglotto.web.dto.DrawResponseDto;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawRequestDTO;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/draw")
@RequiredArgsConstructor
public class DrawRestController {
    private final DrawService drawService;

    @GetMapping("/get-current-draw-info")
    public ApiResponse<DrawResponseDTO.GetCurrentDrawInfoDTO> getCurrentDrawInfo(HttpServletRequest httpServletRequest,
                                                                         @Valid @RequestBody DrawRequestDTO.GetCurrentDrawInfoDTO request) {

        DrawResponseDTO.GetCurrentDrawInfoDTO getCurrentDrawInfoDto = drawService.getCurrentDrawInfo(httpServletRequest, request);

        return ApiResponse.onSuccess(SuccessStatus.DRAW_INFO_FOUND, getCurrentDrawInfoDto);

    }
}