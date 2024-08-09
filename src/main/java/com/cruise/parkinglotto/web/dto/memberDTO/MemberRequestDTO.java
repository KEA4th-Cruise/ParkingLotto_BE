package com.cruise.parkinglotto.web.dto.memberDTO;

import com.cruise.parkinglotto.domain.enums.WorkType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberRequestDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequestDTO {

        @NotBlank(message = "사원명은 필수 입력 값 입니다.")
        @Pattern(regexp="^(?=.*[a-z])[a-zA-Z\\d.]{3,15}$",
                message = "영문 소문자와 숫자가 적어도 1개 이상씩 포함된 3자 ~ 15자의 사원명을 입력해주세요.")
        private String accountId;

        @NotBlank(message = "비밀번호는 필수 입력 값 입니다.")
        @Pattern(regexp="^(?=.*[a-z])(?=.*\\d)[a-zA-Z\\d]{9,16}$",
                message = "영문 소문자와 숫자가 적어도 1개 이상씩 포함된 9자 ~ 16자의 비밀번호를 입력해주세요.")
        private String password;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LogoutRequestDTO {
        private String accountId;
        private String accessToken;
        private String refreshToken;
    }


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyInfoRequestDTO{
        @NotEmpty(message = "차량 번호를 입력해주세요")
        private String carNum;
        @NotEmpty(message = "주소를 입력해주세요")
        private String address;
        @NotNull(message = "근무 타입을 입력해주세요")
        private WorkType workType;
    }
}
