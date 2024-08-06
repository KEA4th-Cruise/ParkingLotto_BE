package com.cruise.parkinglotto.web.dto.registerDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class RegisterResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfoResponseDTO {
        private String nameKo;
        private String employeeNo;
        private String deptPathName;
        private String accountId;
        private String email;
        private String carNum;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MembersResponseDTO {
        private String nameKo;
        private String employeeNo;
        private String deptPathName;
        private String accountId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MembersResponseDTOList {
        private List<RegisterResponseDTO.MembersResponseDTO> memberList;
        Integer listSize;
        Integer totalPage;
        Long totalElements;
        Boolean isFirst;
        Boolean isLast;
    }

}
