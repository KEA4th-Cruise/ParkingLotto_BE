package com.cruise.parkinglotto.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


public class ApplicantResponseDTO {
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WinnerCancelResponseDTO {

        private LocalDateTime canceledAt;
        private String applicantName;
        private String employeeNo;

    }


}
