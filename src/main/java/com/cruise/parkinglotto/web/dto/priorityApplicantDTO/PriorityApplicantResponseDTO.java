package com.cruise.parkinglotto.web.dto.priorityApplicantDTO;

import com.cruise.parkinglotto.domain.enums.ApprovalStatus;
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
}
