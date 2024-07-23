package com.cruise.parkinglotto.web.controller;

import com.cruise.parkinglotto.global.response.ApiResponse;
import com.cruise.parkinglotto.global.response.code.status.SuccessStatus;
import com.cruise.parkinglotto.service.memberService.MemberService;
import com.cruise.parkinglotto.web.dto.memberDTO.MemberRequestDTO;
import com.cruise.parkinglotto.web.dto.memberDTO.MemberResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/login")
    public ApiResponse<MemberResponseDTO.LoginResponseDTO> addParkingSpace(@RequestBody MemberRequestDTO.LoginRequestDTO loginRequestDTO) {
        return ApiResponse.onSuccess(SuccessStatus.MEMBER_LOGIN_SUCCESS, memberService.login(loginRequestDTO));
    }
}
