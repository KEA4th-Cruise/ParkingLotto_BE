package com.cruise.parkinglotto.web.controller;

import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.global.jwt.JwtUtils;
import com.cruise.parkinglotto.global.response.ApiResponse;
import com.cruise.parkinglotto.global.response.code.status.SuccessStatus;
import com.cruise.parkinglotto.service.memberService.MemberService;
import com.cruise.parkinglotto.service.registerService.RegisterService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/register")
@RequiredArgsConstructor
public class RegisterController {

    private final MemberService memberService;
    private final RegisterService registerService;
    private final JwtUtils jwtUtils;

    @Operation(summary = "사용자 등록 요청 API", description = "API 요청 시 토큰에서 유저 정보를 가져오기 때문에 RequestDTO가 필요하지 않습니다. 응답 result 또한 null을 반환합니다.")
    @GetMapping("/request")
    public ApiResponse<Object> requestRegister(HttpServletRequest httpServletRequest) {
        String accountId = jwtUtils.getAccountIdFromRequest(httpServletRequest);
        Member member = memberService.getMemberByAccountId(accountId);
        return ApiResponse.onSuccess(SuccessStatus.REGISTER_REQUEST_SUCCESS, registerService.requestRegister(member));
    }

    @Operation(summary = "사용자 세부 정보 조회 API", description = "관리자가 등록 관리 페이지에서 사용자의 세부 정보를 조회하는 API 입니다.")
    @GetMapping("/member-info/{accountId}")
    public ApiResponse<Object> getMemberInfo(HttpServletRequest httpServletRequest, @PathVariable("accountId") String accountId) {
        return ApiResponse.onSuccess(SuccessStatus.REGISTER_MEMBER_INFO_FOUND, registerService.getMemberInfo(accountId));
    }
}
