package com.cruise.parkinglotto.web.controller;

import com.cruise.parkinglotto.domain.Applicant;

import com.cruise.parkinglotto.global.exception.handler.ExceptionHandler;
import com.cruise.parkinglotto.global.jwt.JwtToken;
import com.cruise.parkinglotto.global.jwt.JwtUtils;

import com.cruise.parkinglotto.global.response.ApiResponse;
import com.cruise.parkinglotto.global.response.code.status.SuccessStatus;
import com.cruise.parkinglotto.service.applicantService.ApplicantService;
import com.cruise.parkinglotto.service.memberService.MemberService;
import com.cruise.parkinglotto.web.converter.ApplicantConverter;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpRequest;


@RestController
@RequestMapping("/api/applicant")
@RequiredArgsConstructor
public class ApplicantRestController {
    private final ApplicantService applicantService;
    private final MemberService memberService;
    private final JwtUtils jwtUtils;

//    @GetMapping("/test/get-jwt-token")
//    public String getJwtToken() {
//        return jwtUtils.generateToken()
//    }

    @Operation(summary = "신청자 목록을 조회하는 API 입니다. 페이징을 포함합니다.", description = " RequestParam 으로 drawId와 page 번호를 전송해주세요.")
    @GetMapping("/list")
    public ApiResponse<ApplicantResponseDTO.GetApplicantListResultDTO> getApplicantList(@RequestParam(name = "drawId") Long drawId,
                                                                                        @RequestParam(name = "page") Integer page) {
        Page<Applicant> applicantList = applicantService.getApplicantList(page - 1, drawId);
        return ApiResponse.onSuccess(SuccessStatus.APPLICANT_LIST_FOUND, ApplicantConverter.toGetApplicantListResultDTO(applicantList));
    }
    
    @PostMapping("/my-apply-list/{draw-id}/cancel")
    public ApiResponse<ApplicantResponseDTO.WinnerCancelResponseDTO> cancelApply(@PathVariable("draw-id") Long drawId, HttpServletRequest request) {

        Authentication authentication = jwtUtils.getAuthentication(jwtUtils.resolveToken(request));
        String name = authentication.getName( );
        Long memberId = memberService.getMemberIdByAccountId(name);

        return ApiResponse.onSuccess(SuccessStatus.CANCEL_SUCCESS, applicantService.giveUpMyWinning(memberId,drawId));



    @PatchMapping("/priority-approval")
    public ApiResponse<ApplicantResponseDTO.ApprovePriorityResultDTO> approvePriority(@RequestParam(name = "drawId") Long drawId,
                                                                                      @RequestParam(name = "applicantId") Long applicantId) {
        ApplicantResponseDTO.ApprovePriorityResultDTO approvePriorityResultDTO = applicantService.approvePriority(drawId, applicantId);
        return ApiResponse.onSuccess(SuccessStatus.APPLICANT_PRIORITY_APPROVED, approvePriorityResultDTO);

    }
}
