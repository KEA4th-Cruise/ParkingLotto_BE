package com.cruise.parkinglotto.web.controller;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.global.jwt.JwtUtils;
import com.cruise.parkinglotto.global.kc.ObjectStorageService;
import com.cruise.parkinglotto.global.response.ApiResponse;
import com.cruise.parkinglotto.global.response.code.status.SuccessStatus;
import com.cruise.parkinglotto.service.applicantService.ApplicantService;
import com.cruise.parkinglotto.web.converter.ApplicantConverter;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantRequestDTO;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/applicant")
@RequiredArgsConstructor
public class ApplicantRestController {
    private final ApplicantService applicantService;
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

    @Operation(summary = "사용자가 일반 추첨을(GENERAL) 신청하는 api입니다.", description = "파일 리스트와 적절한 DTO를 인터페이스 명세서를 참조해서 넣어주세요.")
    @PostMapping("/apply/general")
    public ApiResponse<?> drawApply(HttpServletRequest httpServletRequest, @RequestPart(value = "certificateDocs", required = false) List<MultipartFile> certificateDocs,
                                    @RequestPart(value = "applyDrawRequestDTO", required = true) @Valid ApplicantRequestDTO.GeneralApplyDrawRequestDTO applyDrawRequestDTO) {
        String accountId = jwtUtils.getAccountIdFromRequest(httpServletRequest);
        applicantService.drawApply(certificateDocs, applyDrawRequestDTO, accountId);
        return ApiResponse.onSuccess(SuccessStatus.APPLICANT_SUCCESS, null);
    }

}
