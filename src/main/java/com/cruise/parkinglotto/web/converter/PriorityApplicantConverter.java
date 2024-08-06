package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.domain.*;
import com.cruise.parkinglotto.domain.enums.ApprovalStatus;
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

    public static PriorityApplicantResponseDTO.GetPriorityApplicantListResultDTO toGetPriorityApplicantListResultDTO(Integer totalSlots, Page<PriorityApplicant> priorityApplicantPage) {
        List<PriorityApplicantResponseDTO.GetPriorityApplicantResultDTO> getPriorityApplicantResultDTOList = priorityApplicantPage.stream()
                .map(PriorityApplicantConverter::toGetPriorityApplicantResultDTO).toList();
        return PriorityApplicantResponseDTO.GetPriorityApplicantListResultDTO.builder()
                .totalSlots(totalSlots)
                .applicantsCount(priorityApplicantPage.getSize())
                .priorityApplicantList(getPriorityApplicantResultDTOList)
                .isFirst(priorityApplicantPage.isFirst())
                .isLast(priorityApplicantPage.isLast())
                .totalElements(priorityApplicantPage.getTotalElements())
                .totalPage(priorityApplicantPage.getTotalPages())
                .listSize(priorityApplicantPage.getSize())
                .build();
    }

    public static PriorityApplicantResponseDTO.ApprovePriorityResultDTO toApprovePriorityResultDTO(PriorityApplicant priorityApplicant) {
        return PriorityApplicantResponseDTO.ApprovePriorityResultDTO.builder()
                .priorityApplicantId(priorityApplicant.getId())
                .approvalStatus(priorityApplicant.getApprovalStatus())
                .build();
    }

    public static PriorityApplicantResponseDTO.GetPriorityApplicantDetailsResultDTO toGetPriorityApplicantDetailsResultDTO(PriorityApplicant priorityApplicant,
                                                                                                                           List<CertificateDocs> certificateDocsList) {

        Member member = priorityApplicant.getMember();
        return PriorityApplicantResponseDTO.GetPriorityApplicantDetailsResultDTO.builder()
                .nameKo(member.getNameKo())
                .accountId(member.getAccountId())
                .employeeNo(member.getEmployeeNo())
                .deptPathName((member.getDeptPathName()))
                .certificateFileList(certificateDocsList.stream()
                        .map(CertificateDocsConverter::toCertificateFileDTO)
                        .toList())
                .approvalStatus(priorityApplicant.getApprovalStatus())
                .build();
    }

    public static PriorityApplicantResponseDTO.RejectPriorityResultDTO toRejectPriorityResultDTO(PriorityApplicant priorityApplicant) {
        return PriorityApplicantResponseDTO.RejectPriorityResultDTO.builder()
                .priorityApplicantId(priorityApplicant.getId())
                .approvalStatus(priorityApplicant.getApprovalStatus())
                .build();
    }

    public static PriorityApplicant makeInitialPriorityApplicantObject(Member member, Draw draw, String carNum) {
        return PriorityApplicant.builder()
                .approvalStatus(ApprovalStatus.PENDING)
                .parkingSpaceId(null)
                .carNum(carNum)
                .member(member)
                .draw(draw)
                .build();
    }
}
