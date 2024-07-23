package com.cruise.parkinglotto.global.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtTokenValidationResult {

    private boolean isValid; // 이 부분은 wrapper 클래스로 사용하면 오류가 발생해서 기본 타입을 사용했습니다.
    private String errorMessage;

}
