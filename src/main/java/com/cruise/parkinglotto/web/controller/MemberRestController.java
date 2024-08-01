package com.cruise.parkinglotto.web.controller;

import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.global.jwt.JwtUtils;
import com.cruise.parkinglotto.global.response.ApiResponse;
import com.cruise.parkinglotto.global.response.code.status.SuccessStatus;
import com.cruise.parkinglotto.service.memberService.MemberService;
import com.cruise.parkinglotto.web.dto.memberDTO.MemberRequestDTO;
import com.cruise.parkinglotto.web.dto.memberDTO.MemberResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberRestController {

    private final MemberService memberService;
    private final JwtUtils jwtUtils;

    @Operation(summary = "로그인 API", description = "accountId, password를 loginRequestDTO에 담아 요청을 보내면 등록 여부와 토큰을 반환합니다.")
    @PostMapping("/login")
    public ApiResponse<MemberResponseDTO.LoginResponseDTO> login(@RequestBody MemberRequestDTO.LoginRequestDTO loginRequestDTO) {
        return ApiResponse.onSuccess(SuccessStatus.MEMBER_LOGIN_SUCCESS, memberService.login(loginRequestDTO));
    }
  
    @Operation(summary = "로그아웃 API", description = "accountId, accessToken, refreshToken을 logoutRequestDTO에 담아 요청을 보내면 로그아웃 시간을 반환합니다.")
    @PostMapping("/logout")
    public ApiResponse<MemberResponseDTO.LogoutResponseDTO> logout(@RequestBody MemberRequestDTO.LogoutRequestDTO logoutRequestDTO) {
        return ApiResponse.onSuccess(SuccessStatus.MEMBER_LOGOUT_SUCCESS, memberService.logout(logoutRequestDTO));
    }

    @Operation(summary = "내가 입력한 정보 API",description = "내가 이전에 입력했던 회원정보를 불러오는 API 입니다. 주요 정보로는 차량번호, 첨부 파일, 거주지주소, 근무타입이 있습니다.")
    @GetMapping("/my-info")
    public ApiResponse<MemberResponseDTO.MyInfoResponseDTO> getMyInfo(HttpServletRequest httpServletRequest) {

        String accountIdFromRequest = jwtUtils.getAccountIdFromRequest(httpServletRequest);
        Member memberByAccountId = memberService.getMemberByAccountId(accountIdFromRequest);
        return ApiResponse.onSuccess(SuccessStatus.MEMBER_INFO_FOUND, memberService.getMyInfo(memberByAccountId.getId()));

    }

}
