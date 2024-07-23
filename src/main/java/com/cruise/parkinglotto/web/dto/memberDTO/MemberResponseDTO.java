package com.cruise.parkinglotto.web.dto.memberDTO;

import com.cruise.parkinglotto.domain.enums.EnrollmentStatus;
import com.cruise.parkinglotto.global.jwt.JwtToken;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponseDTO {
        private JwtToken jwtToken;
        private EnrollmentStatus enrollmentStatus;
    }
}
