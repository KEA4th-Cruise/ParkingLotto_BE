package com.cruise.parkinglotto.web.dto.priorityApplicantDTO;

import com.cruise.parkinglotto.domain.enums.ApprovalStatus;
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
        Integer applicantsCount;
        Integer totalSlots;
        private List<PriorityApplicantResponseDTO.GetPriorityApplicantResultDTO> priorityApplicantList;
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
        private Long priorityApplicantId;
        private ApprovalStatus approvalStatus;
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
        private List<CertificateDocsRequestDTO.CertificateFileDTO> certificateFileList;
        private ApprovalStatus approvalStatus;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RejectPriorityResultDTO {
        private Long priorityApplicantId;
        private ApprovalStatus approvalStatus;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignPriorityResultDTO {
        private Long priorityApplicantId;
        private Long parkingSpaceId;
        private ApprovalStatus approvalStatus;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class  AssignPriorityResultListDTO {
        private List<AssignPriorityResultDTO> assignPriorityResultList;
    }
}
