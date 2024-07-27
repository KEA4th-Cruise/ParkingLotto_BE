package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.web.dto.registerDTO.RegisterResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class RegisterConverter {

    public static RegisterResponseDTO.MemberInfoResponseDTO toMemberInfoResponseDTO(Member member) {
        return RegisterResponseDTO.MemberInfoResponseDTO.builder()
                .nameKo(member.getNameKo())
                .employeeNo(member.getEmployeeNo())
                .deptPathName(member.getDeptPathName())
                .accountId(member.getAccountId())
                .email(member.getEmail())
                .carNum(member.getCarNum())
                .build();
    }

    public static RegisterResponseDTO.MembersResponseDTO toMembersResponseDTO(Member member) {
        return RegisterResponseDTO.MembersResponseDTO.builder()
                .accountId(member.getAccountId())
                .deptPathName(member.getDeptPathName())
                .employeeNo(member.getEmployeeNo())
                .nameKo(member.getNameKo())
                .build();
    }

    public static List<RegisterResponseDTO.MembersResponseDTO> toMembersResponseDTOList(List<Member> members) {
        return members.stream()
                .map(RegisterConverter::toMembersResponseDTO)
                .collect(Collectors.toList());
    }
}
