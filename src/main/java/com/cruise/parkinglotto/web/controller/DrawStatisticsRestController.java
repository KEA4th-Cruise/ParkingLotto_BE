package com.cruise.parkinglotto.web.controller;

import com.cruise.parkinglotto.domain.DrawStatistics;
import com.cruise.parkinglotto.global.response.ApiResponse;
import com.cruise.parkinglotto.global.response.code.status.SuccessStatus;
import com.cruise.parkinglotto.service.drawStatisticsService.DrawStatisticsService;
import com.cruise.parkinglotto.web.converter.DrawStatisticsConverter;
import com.cruise.parkinglotto.web.dto.drawStatisticsDTO.DrawStatisticsResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/draw-statistics")
@RequiredArgsConstructor
public class DrawStatisticsRestController {
    private final DrawStatisticsService drawStatisticsService;

    @Operation(summary = "(메인페이지용) 최근 5회차 일반 추첨의 drawId, 제목, 경쟁률을 응답하는 API 입니다.")
    @GetMapping("/recent")
    public ApiResponse<DrawStatisticsResponseDTO.DrawCompetitionRateListDTO> getRecentDrawCompetitionRate() {
        List<DrawStatistics> drawStatisticsList = drawStatisticsService.getRecentDrawStatistics();
        return ApiResponse.onSuccess(SuccessStatus.DRAW_STATISTICS_FOUND, DrawStatisticsConverter.toDrawCompetitionRateListDTO(drawStatisticsList));
    }
}
