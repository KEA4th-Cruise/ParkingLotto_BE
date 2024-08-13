package com.cruise.parkinglotto.web.controller;

import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.global.jwt.JwtToken;
import com.cruise.parkinglotto.global.jwt.JwtUtils;
import com.cruise.parkinglotto.global.response.ApiResponse;
import com.cruise.parkinglotto.global.response.code.status.SuccessStatus;
import com.cruise.parkinglotto.service.applicantService.ApplicantService;
import com.cruise.parkinglotto.service.drawService.DrawService;
import com.cruise.parkinglotto.service.memberService.MemberService;
import com.cruise.parkinglotto.service.parkingSpaceService.ParkingSpaceService;
import com.cruise.parkinglotto.web.converter.DrawConverter;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO;
import com.cruise.parkinglotto.web.dto.drawDTO.DrawResponseDTO;
import com.cruise.parkinglotto.web.dto.memberDTO.MemberRequestDTO;
import com.cruise.parkinglotto.web.dto.memberDTO.MemberResponseDTO;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberRestController {

    private final MemberService memberService;
    private final JwtUtils jwtUtils;
    private final ParkingSpaceService parkingSpaceService;
    private final ApplicantService applicantService;
    private final DrawService drawService;

    @Operation(summary = "로그인 API", description = "accountId, password를 loginRequestDTO에 담아 요청을 보내면 등록 여부와 토큰을 반환합니다(신해철).")
    @PostMapping("/login")
    public ApiResponse<MemberResponseDTO.LoginResponseDTO> login(@RequestBody @Valid MemberRequestDTO.LoginRequestDTO loginRequestDTO, HttpServletResponse httpServletResponse) {
        MemberResponseDTO.LoginResponseDTO loginResponseDTO = memberService.login(loginRequestDTO);

        // access token과 refresh token을 각각의 쿠키에 담아서 보냄
        JwtToken jwtToken = loginResponseDTO.getJwtToken();
        setCookie(httpServletResponse, "accessToken", jwtToken.getAccessToken(), 60 * 60 * 24); // 1일 (초, 분, 시)
        setCookie(httpServletResponse, "refreshToken", jwtToken.getRefreshToken(), 60 * 60 * 24 * 7); // 7일 (초, 분, 시, 일)

        return ApiResponse.onSuccess(SuccessStatus.MEMBER_LOGIN_SUCCESS, loginResponseDTO);
    }

    @Operation(summary = "로그아웃 API", description = "요청을 보내면 로그아웃 시간을 반환합니다. 쿠키에 있는 리프레시 토큰을 블랙리스트에 저장합니다. (신해철)")
    @GetMapping("/logout")
    public ApiResponse<MemberResponseDTO.LogoutResponseDTO> logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String accessToken = jwtUtils.getTokenInCookie(httpServletRequest, "accessToken");
        String refreshToken = jwtUtils.getTokenInCookie(httpServletRequest, "refreshToken");

        setCookie(httpServletResponse, "accessToken", accessToken, 0); // 쿠키 만료
        setCookie(httpServletResponse, "refreshToken", refreshToken, 0); // 쿠키 만료

        return ApiResponse.onSuccess(SuccessStatus.MEMBER_LOGOUT_SUCCESS, memberService.logout(refreshToken));
    }

    @Operation(summary = "토큰 재발급 API", description = "access token이 만료된 경우 refresh token을 사용하여 자동 로그인을 해주는 API 입니다.(신해철)")
    @GetMapping("/refresh")
    public ApiResponse<MemberResponseDTO.RefreshResponseDTO> refreshAccessToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        System.out.println("MemberRestController.refreshAccessToken");

        String refreshToken = jwtUtils.getTokenInCookie(httpServletRequest, "refreshToken");
        MemberResponseDTO.RefreshResponseDTO refreshResponseDTO = memberService.recreateToken(refreshToken);
        setCookie(httpServletResponse, "accessToken", refreshResponseDTO.getAccessToken(), 60 * 60 * 24); // 1일 (초, 분, 시)
        return ApiResponse.onSuccess(SuccessStatus.MEMBER_REFRESH_TOKEN_SUCCESS, refreshResponseDTO);
    }

    @Operation(summary = "사용자 입력정보 조회 API", description = "로그인한 사용자의 입력정보를 불러오는 API 입니다. 주요 정보로는 차량번호, 증명서류, 거주지주소, 근무타입이 있습니다.(최준범)")
    @GetMapping("/info")
    public ApiResponse<MemberResponseDTO.MyInfoResponseDTO> getMyInfo(HttpServletRequest httpServletRequest) {

        String accountIdFromRequest = jwtUtils.getAccountIdFromRequest(httpServletRequest);
        Member memberByAccountId = memberService.getMemberByAccountId(accountIdFromRequest);
        return ApiResponse.onSuccess(SuccessStatus.MEMBER_INFO_FOUND, memberService.getMyInfo(memberByAccountId.getId()));

    }

    @Operation(summary = "내가 입력한 정보 수정 API", description = "내가 입력한 회원정보를 수정하는 API 입니다. RequestPart 에 myInfoRequestDTO 를 넣고, 보내야하는 문서들을 넣어서 보내주세요. Swagger 참고하시면 됩니다 (최준범)")
    @PatchMapping(value = "/info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<MemberResponseDTO.MyInfoResponseDTO> updateMyInfo(HttpServletRequest httpServletRequest,
                                                                         @RequestPart(value = "applyDrawRequestDTO", required = true) @Valid MemberRequestDTO.MyInfoRequestDTO myInfoRequestDTO,
                                                                         @RequestPart(required = false, value = "certificateDocs") @Valid List<MultipartFile> certificateDocs) {
        String accountIdFromRequest = jwtUtils.getAccountIdFromRequest(httpServletRequest);
        Member memberByAccountId = memberService.getMemberByAccountId(accountIdFromRequest);
        MemberResponseDTO.MyInfoResponseDTO myInfoResponseDTO = memberService.updateMyInfo(memberByAccountId.getId(), myInfoRequestDTO, certificateDocs);
        return ApiResponse.onSuccess(SuccessStatus.MEMBER_INFO_UPDATED, myInfoResponseDTO);
    }

    @Operation(summary = "내가 배정받은 주차공간 정보를 조회하는 API 입니다.", description = " Pathvariable 로 drawId 를 보내주면 해당 회차에 내 주차공간 정보를 보내줍니다. 주요 정보는 주차공간 주소, 구역이름, 이미지입니다.(최준범)")
    @GetMapping("/{drawId}/my-space")
    public ApiResponse<ParkingSpaceResponseDTO.ParkingSpaceInfoResponseDTO> getParkingSpaceInfo(@PathVariable("drawId") Long drawId, HttpServletRequest httpServletRequest) {

        String accountIdFromRequest = jwtUtils.getAccountIdFromRequest(httpServletRequest);
        Member findMember = memberService.getMemberByAccountId(accountIdFromRequest);

        return ApiResponse.onSuccess(SuccessStatus.PARKING_SPACE_INFO_FOUND, parkingSpaceService.findParkingSpaceInfo(findMember.getId(), drawId));
    }


    @Operation(summary = "내가 신청했던 추첨 목록을 조회하는 API 입니다. 페이징을 포함합니다", description = " RequestParam 으로 조회하고 싶은 page 번호를 전송해 주세요.(최준범, 이윤서(수정자)) ")
    @GetMapping("/applied/draws")
    public ApiResponse<DrawResponseDTO.GetAppliedDrawListResultDTO> getMyApplyList(HttpServletRequest httpServletRequest, @RequestParam(name = "page") Integer page) {
        String accountIdFromRequest = jwtUtils.getAccountIdFromRequest(httpServletRequest);
        Page<DrawResponseDTO.GetAppliedDrawResultDTO> appliedDrawList = drawService.getAppliedDrawList(accountIdFromRequest, page - 1);
        return ApiResponse.onSuccess(SuccessStatus.APPLICANT_APPLY_LIST_FOUND, DrawConverter.toGetAppliedDrawListResultDTO(appliedDrawList));
    }

    @Operation(summary = "특정 회차 결과 조회 API", description = "내가 신청했던 회차중 특정 회차의 결과를 조회하는 API 입니다, PathVariable 으로 drawId 를 보내주세요.(최준범)")
    @GetMapping("/applied/draws/{drawId}")
    public ApiResponse<ApplicantResponseDTO.MyApplyInfoDTO> getMyApplyInfo(@PathVariable("drawId") Long drawId, HttpServletRequest httpServletRequest) {

        String accountIdFromRequest = jwtUtils.getAccountIdFromRequest(httpServletRequest);
        Member memberByAccountId = memberService.getMemberByAccountId(accountIdFromRequest);
        return ApiResponse.onSuccess(SuccessStatus.APPLICANT_APPLY_INFO_FOUND, applicantService.getMyApplyInfo(memberByAccountId.getId(), drawId));

    }

    @Operation(summary = "내가 입력한 정보 저장 API", description = "내가 입력한 회원정보를 저장하는 API 입니다. RequestBody 에 myInfoRequestDTO 를 넣고, 보내야하는 문서들을 넣어서 보내주세요. Swagger 참고하시면 됩니다")
    @PostMapping(value = "/info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<MemberResponseDTO.MyInfoResponseDTO> saveMyInfo(HttpServletRequest httpServletRequest,
                                                                       @RequestPart(value = "applyDrawRequestDTO", required = true) @Valid MemberRequestDTO.MyInfoRequestDTO myInfoRequestDTO,
                                                                       @RequestPart(required = false, value = "certificateDocs") @Valid List<MultipartFile> certificateDocs) {
        String accountIdFromRequest = jwtUtils.getAccountIdFromRequest(httpServletRequest);
        Member memberByAccountId = memberService.getMemberByAccountId(accountIdFromRequest);
        MemberResponseDTO.MyInfoResponseDTO myInfoResponseDTO = memberService.saveMyInfo(memberByAccountId.getId(), myInfoRequestDTO, certificateDocs);
        return ApiResponse.onSuccess(SuccessStatus.MEMBER_INFO_SAVED, myInfoResponseDTO);
    }

    private void setCookie(HttpServletResponse httpServletResponse, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        httpServletResponse.addCookie(cookie);
    }
}