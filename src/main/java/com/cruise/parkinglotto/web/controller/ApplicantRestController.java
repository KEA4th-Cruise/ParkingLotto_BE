package com.cruise.parkinglotto.web.controller;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.global.jwt.JwtUtils;
import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.global.response.ApiResponse;
import com.cruise.parkinglotto.global.response.code.status.SuccessStatus;
import com.cruise.parkinglotto.service.applicantService.ApplicantService;
import com.cruise.parkinglotto.service.memberService.MemberService;
import com.cruise.parkinglotto.web.converter.ApplicantConverter;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantRequestDTO;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/api/applicant")
@RequiredArgsConstructor
public class ApplicantRestController {
    private final ApplicantService applicantService;
    private final JwtUtils jwtUtils;
    private final MemberService memberService;


    @Operation(summary = "일반 추첨 신청자 목록을 조회하는 API 입니다. 페이징을 포함합니다.", description = " RequestParam 으로 drawId와 page 번호를 전송해주세요.")
    @GetMapping("/list")
    public ApiResponse<ApplicantResponseDTO.GetApplicantListResultDTO> getApplicantList(@RequestParam(name = "drawId") Long drawId,
                                                                                        @RequestParam(name = "page") Integer page) {
        Page<Applicant> applicantList = applicantService.getApplicantList(page - 1, drawId);
        return ApiResponse.onSuccess(SuccessStatus.APPLICANT_LIST_FOUND, ApplicantConverter.toGetApplicantListResultDTO(applicantList));
    }

    @GetMapping("/my-apply-list")
    public ApiResponse<List<ApplicantResponseDTO.GetMyApplyResultDTO>> getMyApplyList(HttpServletRequest httpServletRequest) {
        String accountIdFromRequest = jwtUtils.getAccountIdFromRequest(httpServletRequest);
        Member memberByAccountId = memberService.getMemberByAccountId(accountIdFromRequest);
        List<ApplicantResponseDTO.GetMyApplyResultDTO> applyResultList = applicantService.getApplyResultList(memberByAccountId.getId());
        return ApiResponse.onSuccess(SuccessStatus.APPLICANT_APPLY_LIST_FOUND, applyResultList);
    }

    @Operation(summary = "특정 회차 결과 조회 API", description = "내가 신청했던 회차중 특정 회차의 결과를 조회하는 API 입니다, PathVariable 으로 drawId 를 보내주세요 ")
    @GetMapping("/{drawId}/my-apply")
    public ApiResponse<ApplicantResponseDTO.MyApplyInfoDTO> getMyApplyInfo(@PathVariable("drawId") Long drawId, HttpServletRequest httpServletRequest) {

        String accountIdFromRequest = jwtUtils.getAccountIdFromRequest(httpServletRequest);
        Member memberByAccountId = memberService.getMemberByAccountId(accountIdFromRequest);
        return ApiResponse.onSuccess(SuccessStatus.APPLICANT_APPLY_INFO_FOUND, applicantService.getMyApplyInfo(memberByAccountId.getId(), drawId));

    }

    @Operation(summary = "사용자가 일반 추첨을(GENERAL) 신청하는 api입니다.", description = "파일 리스트와 적절한 DTO를 인터페이스 명세서를 참조해서 넣어주세요.")
    @PostMapping(value = "/apply/general",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> drawApply(HttpServletRequest httpServletRequest, @RequestPart(value = "certificateDocs", required = false) @Parameter(description = "업로드할 인증서 문서 리스트") List<MultipartFile> certificateDocs,
                                    @RequestPart(value = "applyDrawRequestDTO", required = true) @Parameter(description = "일반 추첨 신청에 필요한 요청 데이터") @Valid ApplicantRequestDTO.GeneralApplyDrawRequestDTO applyDrawRequestDTO) {
        String accountId = jwtUtils.getAccountIdFromRequest(httpServletRequest);
        applicantService.drawApply(certificateDocs, applyDrawRequestDTO, accountId);
        return ApiResponse.onSuccess(SuccessStatus.APPLICANT_SUCCESS, null);
    }

}
