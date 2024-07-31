package com.cruise.parkinglotto.web.controller;

import com.cruise.parkinglotto.domain.PriorityApplicant;
import com.cruise.parkinglotto.domain.enums.ApprovalStatus;
import com.cruise.parkinglotto.global.response.ApiResponse;
import com.cruise.parkinglotto.global.response.code.status.SuccessStatus;
import com.cruise.parkinglotto.service.priorityApplicantService.PriorityApplicantService;
import com.cruise.parkinglotto.web.converter.PriorityApplicantConverter;
import com.cruise.parkinglotto.web.dto.priorityApplicantDTO.PriorityApplicantResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/priority-applicant")
@RequiredArgsConstructor
public class PriorityApplicantRestController {
    private final PriorityApplicantService priorityApplicantService;

    @Operation(summary = "우대 신청 신청자 목록을 조회하는 API 입니다. 페이징을 포함합니다.", description = "PathVariable로 approvalStatus(PENDING, APPROVED, REJECTED)를 전송해주세요.  RequestParam 으로 drawId와 page 번호를 전송해주세요.")
    @GetMapping("/list/{approvalStatus}")
    public ApiResponse<PriorityApplicantResponseDTO.GetPriorityApplicantListResultDTO> getApplicantList(@PathVariable(name = "approvalStatus") ApprovalStatus approvalStatus,
                                                                                                        @RequestParam(name = "drawId") Long drawId,
                                                                                                        @RequestParam(name = "page") Integer page) {
        Page<PriorityApplicant> priorityApplicantList = priorityApplicantService.getPriorityApplicantList(page - 1, drawId, approvalStatus);
        return ApiResponse.onSuccess(SuccessStatus.PRIORITY_APPLICANT_LIST_FOUND, PriorityApplicantConverter.toGetPriorityApplicantListResultDTO(priorityApplicantList));
    }

    @Operation(summary = "우대 신청을 승인하는 API 입니다.", description = " RequestParam 으로 drawId와 priorityApplicantId 번호를 전송해주세요.")
    @PatchMapping("/approval")
    public ApiResponse<PriorityApplicantResponseDTO.ApprovePriorityResultDTO> approvePriority(@RequestParam(name = "drawId") Long drawId,
                                                                                              @RequestParam(name = "priorityApplicantId") Long priorityApplicantId) {
        PriorityApplicantResponseDTO.ApprovePriorityResultDTO approvePriorityResultDTO = priorityApplicantService.approvePriority(drawId, priorityApplicantId);
        return ApiResponse.onSuccess(SuccessStatus.PRIORITY_APPLICANT_APPROVED, approvePriorityResultDTO);
    }
}
