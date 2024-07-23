package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public class ApplicantConverter {

    public static ApplicantResponseDTO.GetApplicantResultDTO toGetApplicantResultDTO(Applicant applicant) {
        Member member = applicant.getMember();
        return ApplicantResponseDTO.GetApplicantResultDTO.builder()
                .applicantId(applicant.getId())
                .memberId(member.getId())
                .deptPathName(member.getDeptPathName())
                .employeeNo(member.getEmployeeNo())
                .nameKo(member.getNameKo())
                .build();
    }

    public static ApplicantResponseDTO.GetApplicantListResultDTO toGetApplicantListResultDTO(Page<Applicant> applicantPage){
        List<ApplicantResponseDTO.GetApplicantResultDTO> getApplicantResultDTOList = applicantPage.stream()
                .map(ApplicantConverter::toGetApplicantResultDTO).toList();
        return ApplicantResponseDTO.GetApplicantListResultDTO.builder()
                .getApplicantResultDTOList(getApplicantResultDTOList)
                .isFirst(applicantPage.isFirst())
                .isLast(applicantPage.isLast())
                .totalElements(applicantPage.getTotalElements())
                .totalPage(applicantPage.getTotalPages())
                .listSize(applicantPage.getSize())
                .build();
    }
}
