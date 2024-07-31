package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.domain.PriorityApplicant;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO;
import com.cruise.parkinglotto.web.dto.priorityApplicantDTO.PriorityApplicantResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public class PriorityApplicantConverter {

    public static PriorityApplicantResponseDTO.GetPriorityApplicantResultDTO toGetPriorityApplicantResultDTO(PriorityApplicant priorityApplicant) {
        Member member = priorityApplicant.getMember();
        return PriorityApplicantResponseDTO.GetPriorityApplicantResultDTO.builder()
                .priorityApplicantId(priorityApplicant.getId())
                .approvalStatus(priorityApplicant.getApprovalStatus())
                .memberId(member.getId())
                .deptPathName(member.getDeptPathName())
                .employeeNo(member.getEmployeeNo())
                .nameKo(member.getNameKo())
                .build();
    }

    public static PriorityApplicantResponseDTO.GetPriorityApplicantListResultDTO toGetPriorityApplicantListResultDTO(Page<PriorityApplicant> priorityApplicantPage) {
        List<PriorityApplicantResponseDTO.GetPriorityApplicantResultDTO> getPriorityApplicantResultDTOList = priorityApplicantPage.stream()
                .map(PriorityApplicantConverter::toGetPriorityApplicantResultDTO).toList();
        return PriorityApplicantResponseDTO.GetPriorityApplicantListResultDTO.builder()
                .getPriorityApplicantResultDTOList(getPriorityApplicantResultDTOList)
                .isFirst(priorityApplicantPage.isFirst())
                .isLast(priorityApplicantPage.isLast())
                .totalElements(priorityApplicantPage.getTotalElements())
                .totalPage(priorityApplicantPage.getTotalPages())
                .listSize(priorityApplicantPage.getSize())
                .build();
    }

    public static PriorityApplicantResponseDTO.ApprovePriorityResultDTO toApprovePriorityResultDTO(ParkingSpace parkingSpace) {
        return PriorityApplicantResponseDTO.ApprovePriorityResultDTO.builder()
                .parkingSpaceId(parkingSpace.getId())
                .parkingSpaceName(parkingSpace.getName())
                .remainSlots(parkingSpace.getRemainSlots())
                .build();
    }

}
