package com.cruise.parkinglotto.web.controller;

import com.cruise.parkinglotto.global.response.ApiResponse;
import com.cruise.parkinglotto.global.response.code.status.SuccessStatus;
import com.cruise.parkinglotto.service.applicantService.ApplicantServiceImpl;
import com.cruise.parkinglotto.web.dto.ApplicantResponseDTO.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/apply")
public class ApplicantController {

    private final ApplicantServiceImpl applicantService;
    @PostMapping("/cancel/{memberId}")
    public ApiResponse<WinnerCancelResponseDTO> cancelApply(@PathVariable Long memberId) {

       return ApiResponse.onSuccess(SuccessStatus.CANCEL_SUCCESS,applicantService.giveUpMyWinning(memberId));

    }
}
