package com.cruise.parkinglotto.web.converter;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.domain.enums.WinningStatus;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public class ApplicantConverter {

    public static ApplicantResponseDTO.GetApplicantResultDTO toGetApplicantResultDTO(Applicant applicant) {
        Member member = applicant.getMember( );
        return ApplicantResponseDTO.GetApplicantResultDTO.builder( )
                .applicantId(applicant.getId( ))
                .memberId(member.getId( ))
                .deptPathName(member.getDeptPathName( ))
                .employeeNo(member.getEmployeeNo( ))
                .nameKo(member.getNameKo( ))
                .build( );
    }

    public static ApplicantResponseDTO.GetApplicantListResultDTO toGetApplicantListResultDTO(Page<Applicant> applicantPage) {
        List<ApplicantResponseDTO.GetApplicantResultDTO> getApplicantResultDTOList = applicantPage.stream( )
                .map(ApplicantConverter::toGetApplicantResultDTO).toList( );
        return ApplicantResponseDTO.GetApplicantListResultDTO.builder( )
                .getApplicantResultDTOList(getApplicantResultDTOList)
                .isFirst(applicantPage.isFirst( ))
                .isLast(applicantPage.isLast( ))
                .totalElements(applicantPage.getTotalElements( ))
                .totalPage(applicantPage.getTotalPages( ))
                .listSize(applicantPage.getSize( ))
                .build( );
    }

    public static ApplicantResponseDTO.ApprovePriorityResultDTO toApprovePriorityResultDTO(ParkingSpace parkingSpace) {
        return ApplicantResponseDTO.ApprovePriorityResultDTO.builder( )
                .parkingSpaceId(parkingSpace.getId( ))
                .parkingSpaceName(parkingSpace.getName( ))
                .remainSlots(parkingSpace.getRemainSlots( ))
                .build( );
    }

    public static ApplicantResponseDTO.GetMyApplyResultDTO toGetMyApplyResultDTO(Applicant applicant) {
        String statusData="";
        if(applicant.getWinningStatus()== WinningStatus.CANCELED) {
            statusData = "취소됨";
        }
        else if(applicant.getWinningStatus()==WinningStatus.RESERVE) {
            statusData = "예비 " + applicant.getReserveNum()+"번";
        }
        else if(applicant.getWinningStatus()==WinningStatus.PENDING) {
            statusData = "낙첨";
        }
        else {
            statusData = "당첨";
        }
        return ApplicantResponseDTO.GetMyApplyResultDTO.builder( )
                .drawTitle(applicant.getDraw().getTitle())
                .drawStatisticsId(applicant.getDraw().getDrawStatistics().getId())
                .reserveNum(applicant.getReserveNum())
                .winningStatus(applicant.getWinningStatus())
                .parkingSpaceId(applicant.getParkingSpaceId())
                .statusData(statusData).build( );
    }
}
