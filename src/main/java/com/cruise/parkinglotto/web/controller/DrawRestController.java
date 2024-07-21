package com.cruise.parkinglotto.web.controller;

import com.cruise.parkinglotto.global.response.ApiResponse;
import com.cruise.parkinglotto.global.response.code.status.SuccessStatus;
import com.cruise.parkinglotto.service.drawService.DrawService;
import lombok.RequiredArgsConstructor;
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
}