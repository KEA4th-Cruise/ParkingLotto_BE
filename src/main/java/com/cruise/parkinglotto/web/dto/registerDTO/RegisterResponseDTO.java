package com.cruise.parkinglotto.web.dto.registerDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
