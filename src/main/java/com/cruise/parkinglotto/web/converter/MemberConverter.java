package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.global.jwt.JwtToken;
import com.cruise.parkinglotto.web.dto.memberDTO.MemberResponseDTO;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

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

    public static MemberResponseDTO.RefreshResponseDTO toRefreshResponseDTO(String accessToken) {
        return MemberResponseDTO.RefreshResponseDTO.builder()
                .accessToken(accessToken)
                .build();
    }

    public static MemberResponseDTO.MyInfoResponseDTO toMyInfoResponseDTO(Member member) {
        return MemberResponseDTO.MyInfoResponseDTO.builder()
                .address(member.getWeightDetails().getAddress())
                .carNum(member.getCarNum())
                .workType(member.getWeightDetails().getWorkType())
                .myCertificationInfoResponseDTOS(member.getCertificateDocsList()
                        .stream().map(c -> MemberResponseDTO.MyCertificationInfoResponseDTO.builder()
                                .fileName(c.getFileName())
                                .fileUrl(c.getFileUrl())
                                .certificateDocsId(c.getId()).build())
                        .collect(Collectors.toList()))
                .build();
    }
}
