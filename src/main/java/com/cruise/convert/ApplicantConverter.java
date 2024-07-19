package com.cruise.convert;

import com.cruise.parkinglotto.domain.Applicant;
import com.cruise.parkinglotto.web.dto.applicantDTO.ApplicantResponseDTO;

import java.time.LocalDateTime;

public class ApplicantConverter {

    public static ApplicantResponseDTO.WinnerCancelResponseDTO toWinnerCancelResponseDTO(Applicant applicant) {

        return ApplicantResponseDTO.WinnerCancelResponseDTO.builder( )
                .applicantName(applicant.getMember( ).getNameKo( ))
                .canceledAt(LocalDateTime.now( ))
                .parkingSpaceId(applicant.getParkingSpaceId( ))
                .winningStatus(applicant.getWinningStatus( ))
                .employeeNo(applicant.getMember( ).getEmployeeNo( )).build( );
    }

}
