package com.cruise.parkinglotto.web.controller;

import com.cruise.parkinglotto.domain.Draw;
import com.cruise.parkinglotto.global.response.ApiResponse;
import com.cruise.parkinglotto.global.response.code.status.SuccessStatus;
import com.cruise.parkinglotto.service.drawService.DrawService;
import com.cruise.parkinglotto.web.converter.DrawConverter;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/draw")
@RequiredArgsConstructor
public class DrawRestController {
    private final DrawService drawService;

    @Operation(summary = "해당 회차 현재 신청 현황 조회 API", description = "/current-info/{drawId}중 {drawId}에 유효한 Id를 넣으면 해당 추첨에 관한 정보를 반환합니다.")
    @GetMapping("/current-info/{drawId}")
    public ApiResponse<DrawResponseDTO.GetCurrentDrawInfoDTO> getCurrentDrawInfo(HttpServletRequest httpServletRequest,
                                                                                 @PathVariable Long drawId) {

        DrawResponseDTO.GetCurrentDrawInfoDTO getCurrentDrawInfoDto = drawService.getCurrentDrawInfo(httpServletRequest, drawId);

        return ApiResponse.onSuccess(SuccessStatus.DRAW_INFO_FOUND, getCurrentDrawInfoDto);

    }

    @Operation(summary = "추첨 생성 임시저장 API", description = "화면상 추첨 정보 입력 후 \"다음\" 버튼을 클릭할 때 호출하는 API입니다. 추첨을 생성하기 위해 추첨기간, 사용기간, 세부설명을 입력하고 지도 이미지를 첨부합니다.  drawType을 \"GENERAL\"로 입력시 일반 추첨, \"PRIORITY\"로 입력시 우대 신청입니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<DrawResponseDTO.CreateDrawResultDTO> createDraw(@RequestPart(value = "mapImage", required = true) MultipartFile mapImage,
                                                                       @Valid @RequestPart(value = "createDrawRequestDTO", required = true) DrawRequestDTO.CreateDrawRequestDTO createDrawRequestDTO) {
        Draw draw = drawService.createDraw(mapImage, createDrawRequestDTO);
        return ApiResponse.onSuccess(SuccessStatus.DRAW_INFO_SAVED, DrawConverter.toCreateDrawResultDTO(draw));
    }
}