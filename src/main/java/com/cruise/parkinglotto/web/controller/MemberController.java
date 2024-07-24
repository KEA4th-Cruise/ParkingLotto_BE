package com.cruise.parkinglotto.web.controller;

import com.cruise.parkinglotto.global.response.ApiResponse;
import com.cruise.parkinglotto.global.response.code.status.SuccessStatus;
import com.cruise.parkinglotto.service.memberService.MemberService;
import com.cruise.parkinglotto.web.dto.memberDTO.MemberRequestDTO;
import com.cruise.parkinglotto.web.dto.memberDTO.MemberResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "로그인 API", description = "accountId, password를 loginRequestDTO에 담아 요청을 보내면 등록 여부와 토큰을 반환합니다.")
    @PostMapping("/login")
    public ApiResponse<MemberResponseDTO.LoginResponseDTO> login(@RequestBody MemberRequestDTO.LoginRequestDTO loginRequestDTO) {
        return ApiResponse.onSuccess(SuccessStatus.MEMBER_LOGIN_SUCCESS, memberService.login(loginRequestDTO));
    }

    @Operation(summary = "로그아웃 API", description = "accountId, password를 loginRequestDTO에 담아 요청을 보내면 등록 여부와 토큰을 반환합니다.")
    @PostMapping("/logout")
    public ApiResponse<MemberResponseDTO.LogoutResponseDTO> logout(@RequestBody MemberRequestDTO.LogoutRequestDTO logoutRequestDTO) {
        return ApiResponse.onSuccess(SuccessStatus.MEMBER_LOGOUT_SUCCESS, memberService.logout(logoutRequestDTO));
    }
}
