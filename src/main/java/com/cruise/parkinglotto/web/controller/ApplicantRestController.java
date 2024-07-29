package com.cruise.parkinglotto.web.controller;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.global.jwt.JwtUtils;
import com.cruise.parkinglotto.global.response.ApiResponse;
import com.cruise.parkinglotto.global.response.code.status.SuccessStatus;
import com.cruise.parkinglotto.service.applicantService.ApplicantService;
import com.cruise.parkinglotto.service.memberService.MemberService;
import com.cruise.parkinglotto.web.converter.ApplicantConverter;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/applicant")
@RequiredArgsConstructor
public class ApplicantRestController {
    private final ApplicantService applicantService;
    private final JwtUtils jwtUtils;
    private final MemberService memberService;

    @Operation(summary = "신청자 목록을 조회하는 API 입니다. 페이징을 포함합니다.", description = " RequestParam 으로 drawId와 page 번호를 전송해주세요.")
    @GetMapping("/list")
    public ApiResponse<ApplicantResponseDTO.GetApplicantListResultDTO> getApplicantList(@RequestParam(name = "drawId") Long drawId,
                                                                                        @RequestParam(name = "page") Integer page) {
        Page<Applicant> applicantList = applicantService.getApplicantList(page - 1, drawId);
        return ApiResponse.onSuccess(SuccessStatus.APPLICANT_LIST_FOUND, ApplicantConverter.toGetApplicantListResultDTO(applicantList));
    }


    @PatchMapping("/priority-approval")
    public ApiResponse<ApplicantResponseDTO.ApprovePriorityResultDTO> approvePriority(@RequestParam(name = "drawId") Long drawId,
                                                                                      @RequestParam(name = "applicantId") Long applicantId) {
        ApplicantResponseDTO.ApprovePriorityResultDTO approvePriorityResultDTO = applicantService.approvePriority(drawId, applicantId);
        return ApiResponse.onSuccess(SuccessStatus.APPLICANT_PRIORITY_APPROVED, approvePriorityResultDTO);
    }

    @GetMapping("/my-apply-list")
    public ApiResponse<List<ApplicantResponseDTO.GetMyApplyResultDTO>> getMyApplyList(HttpServletRequest request) {
        String accountIdFromRequest = jwtUtils.getAccountIdFromRequest(request);
        Member memberByAccountId = memberService.getMemberByAccountId(accountIdFromRequest);
        List<ApplicantResponseDTO.GetMyApplyResultDTO> applyResultList = applicantService.getApplyResultList(memberByAccountId.getId( ));
        return ApiResponse.onSuccess(SuccessStatus.APPLICANT_APPLY_LIST_FOUND, applyResultList);
    }
}
