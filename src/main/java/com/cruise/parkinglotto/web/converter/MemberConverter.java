package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.domain.CertificateDocs;
import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.domain.WeightDetails;
import com.cruise.parkinglotto.global.jwt.JwtToken;
import com.cruise.parkinglotto.web.dto.memberDTO.MemberResponseDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class MemberConverter {

    public static MemberResponseDTO.LoginResponseDTO toLoginResponseDTO(Member member, JwtToken jwtToken) {
        return MemberResponseDTO.LoginResponseDTO.builder()
                .jwtToken(jwtToken)
                .nameKo(member.getNameKo())
                .accountId(member.getAccountId())
                .employeeNo(member.getEmployeeNo())
                .deptPathName(member.getDeptPathName())
                .enrollmentStatus(member.getEnrollmentStatus())
                .accountType(member.getAccountType())
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


    public static MemberResponseDTO.MyInfoResponseDTO toMyInfoResponseDTO(Member member, WeightDetails weightDetails, List<CertificateDocs> certificateDocs) {

        return MemberResponseDTO.MyInfoResponseDTO.builder()
                .address(weightDetails.getAddress())
                .carNum(member.getCarNum())
                .workType(weightDetails.getWorkType())
                .certificationDocsList(certificateDocs
                        .stream().map(c -> MemberResponseDTO.MyCertificationInfoResponseDTO.builder()
                                .fileName(c.getFileName())
                                .fileUrl(c.getFileUrl())
                                .fileType(c.getFileName().substring(c.getFileName().lastIndexOf(".") + 1).toLowerCase())
                                .certificateDocsId(c.getId())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
