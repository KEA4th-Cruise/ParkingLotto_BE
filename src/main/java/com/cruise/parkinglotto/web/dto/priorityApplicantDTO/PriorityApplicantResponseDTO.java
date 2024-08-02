package com.cruise.parkinglotto.web.dto.priorityApplicantDTO;

import com.cruise.parkinglotto.domain.enums.ApprovalStatus;
import com.cruise.parkinglotto.domain.enums.WorkType;
import com.cruise.parkinglotto.web.dto.CertificateDocsDTO.CertificateDocsRequestDTO;
import lombok.*;

import java.util.List;


public class PriorityApplicantResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetPriorityApplicantResultDTO {
        private Long priorityApplicantId;
        private Long memberId;
        private String employeeNo;
        private String nameKo;
        private String deptPathName;
        private ApprovalStatus approvalStatus;
    }


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetPriorityApplicantListResultDTO {
        private List<PriorityApplicantResponseDTO.GetPriorityApplicantResultDTO> getPriorityApplicantResultDTOList;
        Integer listSize;
        Integer totalPage;
        Long totalElements;
        Boolean isFirst;
        Boolean isLast;
    }


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApprovePriorityResultDTO {
        private Long parkingSpaceId;
        private String parkingSpaceName;
        private Integer remainSlots;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetPriorityApplicantDetailsResultDTO {  //  우대 신청자의 신청내역 확인용
        private String nameKo;
        private String employeeNo;
        private String accountId;
        private String deptPathName;
        private List<CertificateDocsRequestDTO.CertificateFileDTO> certificateFiles;
        private ApprovalStatus approvalStatus;
    }
}
