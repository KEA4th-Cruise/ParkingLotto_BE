package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.web.dto.registerDTO.RegisterResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

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

    public static RegisterResponseDTO.MembersResponseDTOList toMembersResponseDTOList(Page<Member> memberPage) {
        List<RegisterResponseDTO.MembersResponseDTO> membersResponseDTOList = memberPage.stream()
                .map(RegisterConverter::toMembersResponseDTO)
                .toList();

        return RegisterResponseDTO.MembersResponseDTOList.builder()
                .memberResponseDTOList(membersResponseDTOList)
                .isFirst(memberPage.isFirst())
                .isLast(memberPage.isLast())
                .totalElements(memberPage.getTotalElements())
                .totalPage(memberPage.getTotalPages())
                .listSize(memberPage.getSize())
                .build();
    }


}
