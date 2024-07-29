package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.global.jwt.JwtToken;
import com.cruise.parkinglotto.web.dto.memberDTO.MemberResponseDTO;

import java.time.LocalDateTime;

public class MemberConverter {

    public static MemberResponseDTO.LoginResponseDTO toLoginResponseDTO(Member member, JwtToken jwtToken) {
        return MemberResponseDTO.LoginResponseDTO.builder()
                .jwtToken(jwtToken)
                .enrollmentStatus(member.getEnrollmentStatus())
                .build();
    }

    public static MemberResponseDTO.LogoutResponseDTO toLogoutResponseDTO() {
        return MemberResponseDTO.LogoutResponseDTO.builder()
                .logoutAt(LocalDateTime.now())
                .build();
    }
}