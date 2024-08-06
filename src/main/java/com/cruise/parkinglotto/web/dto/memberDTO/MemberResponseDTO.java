package com.cruise.parkinglotto.web.dto.memberDTO;

import com.cruise.parkinglotto.domain.enums.EnrollmentStatus;
import com.cruise.parkinglotto.domain.enums.WorkType;
import com.cruise.parkinglotto.global.jwt.JwtToken;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

public class MemberResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponseDTO {
        private JwtToken jwtToken;
        private String nameKo;
        private String employeeNo;
        private String deptPathName;
        private String accountId;
        private EnrollmentStatus enrollmentStatus;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LogoutResponseDTO {
        private LocalDateTime logoutAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshResponseDTO {
        private String accessToken;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyInfoResponseDTO {
        private String carNum;
        private List<MyCertificationInfoResponseDTO> myCertificationInfoResponseDTOS;
        private String address;
        private WorkType workType;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyCertificationInfoResponseDTO {

        private Long certificateDocsId;
        private String fileUrl;
        private String fileName;

    }

}
