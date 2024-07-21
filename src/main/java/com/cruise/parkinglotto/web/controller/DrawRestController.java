package com.cruise.parkinglotto.web.controller;

import com.cruise.parkinglotto.global.response.ApiResponse;
import com.cruise.parkinglotto.global.response.code.status.SuccessStatus;
import com.cruise.parkinglotto.service.drawService.DrawService;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawRequestDTO;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/draw")
@RequiredArgsConstructor
public class DrawRestController {
    private final DrawService drawService;

    //추첨 실행 후 결과 저장하는 API
    @PostMapping("/execution/{drawId}")
    public ApiResponse<Void> executeDraw(@PathVariable Long drawId) {
        drawService.executeDraw(drawId);
        return ApiResponse.onSuccess(SuccessStatus.DRAW_EXECUTE_RESULT, null);
    }

    //회차의 추첨 결과의 명단을 조회하는 API
    @GetMapping("/result-member-list/{drawId}")
    public ApiResponse<DrawResponseDTO.DrawResultResponseDTO> getDrawResult(HttpServletRequest httpServletRequest, @PathVariable Long drawId) {

        DrawResponseDTO.DrawResultResponseDTO drawResultResponseDTO = drawService.getDrawResult(httpServletRequest, drawId);
        return ApiResponse.onSuccess(SuccessStatus.DRAW_INFO_FOUND, drawResultResponseDTO);
    }

    @Operation(summary = "해당 회차 현재 신청 현황 조회 API", description = "/current-info/{drawId}중 {drawId}에 유효한 Id를 넣으면 해당 추첨에 관한 정보를 반환합니다.")
    @GetMapping("/current-info/{drawId}")
    public ApiResponse<DrawResponseDTO.GetCurrentDrawInfoDTO> getCurrentDrawInfo(HttpServletRequest httpServletRequest,
                                                                                 @PathVariable Long drawId) {

        DrawResponseDTO.GetCurrentDrawInfoDTO getCurrentDrawInfoDto = drawService.getCurrentDrawInfo(httpServletRequest, drawId);

        return ApiResponse.onSuccess(SuccessStatus.DRAW_INFO_FOUND, getCurrentDrawInfoDto);
    }
}