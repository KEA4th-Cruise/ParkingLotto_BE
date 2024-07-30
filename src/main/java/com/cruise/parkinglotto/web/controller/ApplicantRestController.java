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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/applicant")
@RequiredArgsConstructor
public class ApplicantRestController {
    private final ApplicantService applicantService;
    private final MemberService memberService;
    private final JwtUtils jwtUtils;

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

    @Operation(summary = "특정 회차 결과 조회 API", description = "내가 신청했던 회차중 특정 회차의 결과를 조회하는 API 입니다, PathVariable 으로 drawId 를 보내주세요 ")
    @GetMapping("/{drawId}/my-apply")
    public ApiResponse<ApplicantResponseDTO.MyApplyInfoDTO> getMyApplyInfo(@PathVariable("drawId") Long drawId, HttpServletRequest httpServletRequest) {

        String accountIdFromRequest = jwtUtils.getAccountIdFromRequest(httpServletRequest);
        Member memberByAccountId = memberService.getMemberByAccountId(accountIdFromRequest);
        return ApiResponse.onSuccess(SuccessStatus.APPLICANT_APPLY_INFO_FOUND, applicantService.getMyApplyInfo(memberByAccountId.getId(), drawId));

    }
}
