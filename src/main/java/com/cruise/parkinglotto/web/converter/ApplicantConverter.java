package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
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

    public static ApplicantResponseDTO.GetApplicantListResultDTO toGetApplicantListResultDTO(Page<Applicant> applicantPage) {
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
    
    public static ApplicantResponseDTO.WinnerCancelResponseDTO toWinnerCancelResponseDTO(Applicant applicant) {

        return ApplicantResponseDTO.WinnerCancelResponseDTO.builder( )
                .applicantName(applicant.getMember( ).getNameKo( ))
                .canceledAt(LocalDateTime.now( ))
                .parkingSpaceId(applicant.getParkingSpaceId( ))
                .winningStatus(applicant.getWinningStatus( ))
                .employeeNo(applicant.getMember( ).getEmployeeNo( )).build( );

    public static ApplicantResponseDTO.ApprovePriorityResultDTO toApprovePriorityResultDTO(ParkingSpace parkingSpace) {
        return ApplicantResponseDTO.ApprovePriorityResultDTO.builder()
                .parkingSpaceId(parkingSpace.getId())
                .parkingSpaceName(parkingSpace.getName())
                .remainSlots(parkingSpace.getRemainSlots())
                .build();
    }


}
