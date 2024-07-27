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
    private final HttpServletRequest httpServletRequest;

    //추첨 실행 후 결과 저장하는 API
    @Operation(summary = "추첨 실행 API", description = "path variable로 추첨 ID를 넘겨주면 해당 추첨을 실행합니다.")
    @PostMapping("/execution/{drawId}")
    public ApiResponse<Void> executeDraw(@PathVariable("drawId") Long drawId) {
        drawService.executeDraw(drawId);

        return ApiResponse.onSuccess(SuccessStatus.DRAW_EXECUTE_RESULT, null);
    }

    //회차의 추첨 결과의 명단을 조회하는 API
    @Operation(summary = "해당 회차 추첨 결과 조회 API", description = "path variable로 추첨 ID를 넘겨주면 해당 추첨의 결과를 반환합니다.")
    @GetMapping("/result-members/{drawId}")
    public ApiResponse<DrawResponseDTO.DrawResultResponseDTO> getDrawResult(HttpServletRequest httpServletRequest, @PathVariable("drawId") Long drawId) {

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

    @Operation(summary = "추첨 생성 임시저장 API", description = "화면상 추첨 정보 입력 후 \"다음\" 버튼을 클릭할 때 호출하는 API입니다. 추첨을 생성하기 위해 추첨기간, 사용기간, 세부설명을 입력하고 지도 이미지를 첨부합니다.  drawType을 \"GENERAL\"로 입력시 일반 추첨, \"PRIORITY\"로 입력시 우대 신청입니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<DrawResponseDTO.CreateDrawResultDTO> createDraw(@RequestPart(value = "mapImage", required = true) MultipartFile mapImage,
                                                                       @Valid @RequestPart(value = "createDrawRequestDTO", required = true) DrawRequestDTO.CreateDrawRequestDTO createDrawRequestDTO) {
        Draw draw = drawService.createDraw(mapImage, createDrawRequestDTO);
        return ApiResponse.onSuccess(SuccessStatus.DRAW_INFO_SAVED, DrawConverter.toCreateDrawResultDTO(draw));
    }

    @Operation(summary = "추첨 생성 완료 API", description = "path variable로 추첨 생성 임시저장 API에서 응답으로 받은 drawId를 입력해주세요. 추첨 정보와 해당 추첨의 주차 구역 목록을 확인하고 추첨 생성을 완료하는 API 입니다.")
    @PatchMapping("/creation-confirmed/{drawId}")
    public ApiResponse<DrawResponseDTO.ConfirmDrawCreationResultDTO> confirmDrawCreation(@PathVariable Long drawId) {
        DrawResponseDTO.ConfirmDrawCreationResultDTO drawCreationResultDTO = drawService.confirmDrawCreation(drawId);
        return ApiResponse.onSuccess(SuccessStatus.DRAW_CREATION_CONFIRMED, drawCreationResultDTO);
    }

    @Operation(summary = "페이징을 포함한 추첨 시뮬레이션 API", description = "path variable로 drawId, RequestParam으로 seedNum과 페이지를 넘겨주면 추첨 시뮬레이션을 실행합니다.")
    @GetMapping("/{drawId}/simulate")
    public ApiResponse<DrawResponseDTO.SimulateDrawResponseDTO> simulateDraw(@PathVariable("drawId") Long drawId, @RequestParam("seedNum") String seedNum, @RequestParam("page") Integer page) {
        DrawResponseDTO.SimulateDrawResponseDTO simulateDrawResponseDTO = drawService.simulateDraw(drawId, seedNum, page);
        return ApiResponse.onSuccess(SuccessStatus.DRAW_SIMULATE_COMPLETED, simulateDrawResponseDTO);
    }
}